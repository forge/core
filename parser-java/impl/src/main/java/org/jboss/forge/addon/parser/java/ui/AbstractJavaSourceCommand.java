/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractJavaSourceCommand extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Type Name", required = true)
   private UIInput<String> named;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // Setup named
      named.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            if (!Types.isSimpleName(named.getValue()))
               context.addValidationError(named, "Invalid java type name.");
         }
      });

      // Setup targetPackage
      Project project = getSelectedProject(builder);
      if (project != null && project.hasFacet(JavaSourceFacet.class))
      {
         final Set<String> packageNames = new TreeSet<>();
         final JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
         javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource javaResource)
            {
               String packageName = javaSourceFacet.calculatePackage(javaResource);
               packageNames.add(packageName);
            }
         });
         targetPackage.setCompleter(new UICompleter<String>()
         {

            @Override
            public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                     String value)
            {
               Set<String> result = new LinkedHashSet<>();
               for (String packageName : packageNames)
               {
                  if (Strings.isNullOrEmpty(value) || packageName.startsWith(value))
                  {
                     result.add(packageName);
                  }
               }
               return result;
            }
         });
      }
      builder.add(targetPackage).add(named);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New " + getType())
               .description("Creates a new Java " + getType())
               .category(Categories.create("Java"));
   }

   /**
    * Get the type for which this command should create a new source file. ("Class", "Enum", "Interface", etc.)
    */
   protected abstract String getType();

   /**
    * Get the {@link JavaSource} type for which this command should create a new source file.
    */
   protected abstract Class<? extends JavaSource<?>> getSourceType();

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaSource<?> source = JavaParser.create(getSourceType()).setName(named.getValue());
      JavaResource javaResource;
      if (targetPackage.hasValue() || targetPackage.hasDefaultValue())
      {
         source.setPackage(targetPackage.getValue());
      }
      else
      {
         source.setPackage(javaSourceFacet.getBasePackage());
      }
      if (source.hasSyntaxErrors())
      {
         UIOutput output = uiContext.getProvider().getOutput();
         PrintStream err = output.err();
         err.println("Syntax Errors:");
         for (SyntaxError error : source.getSyntaxErrors())
         {
            err.println(error);
         }
         err.println();
         return Results.fail("Syntax Errors found. See above");
      }
      else
      {
         javaResource = javaSourceFacet.saveJavaSource(source);
      }
      uiContext.setSelection(javaResource);
      return Results.success(getType() + " " + source.getQualifiedName() + " was created");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   protected UIInput<String> getTargetPackage()
   {
      return targetPackage;
   }

   protected UIInput<String> getNamed()
   {
      return named;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
