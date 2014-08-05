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
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.ResourceException;
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
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.SyntaxError;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.util.Strings;
import org.jboss.forge.roaster.model.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(value = JavaSourceFacet.class, type = FacetConstraintType.REQUIRED)
})
public abstract class AbstractJavaSourceCommand<SOURCETYPE extends JavaSource<?>> extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER, description = "The package name where this type will be created")
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Type Name", required = true, description = "The type name")
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Overwrite", description = "The overwrite flag that is used if the class already exists.", defaultValue = "false")
   private UIInput<Boolean> overwrite;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder);
      final JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
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

      overwrite.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call()
         {
            if (named.getValue() == null)
            {
               return false;
            }
            return classExists(javaSourceFacet);
         }
      });

      // Setup targetPackage

      if (project.hasFacet(JavaSourceFacet.class))
      {
         final Set<String> packageNames = new TreeSet<>();

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
      targetPackage.setDefaultValue(calculateDefaultPackage(builder.getUIContext()));
      builder.add(targetPackage).add(named).add(overwrite);
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
   protected abstract Class<SOURCETYPE> getSourceType();

   private boolean classExists(JavaSourceFacet javaSourceFacet)
   {
      JavaSource<?> source = buildJavaSource(javaSourceFacet);
      if (source == null)
      {
         return false;
      }
      boolean classAlreadyExists;
      try
      {
         JavaResource parsedJavaResource = javaSourceFacet.getJavaResource(source);
         classAlreadyExists = parsedJavaResource != null && parsedJavaResource.exists();
      }
      catch (ResourceException ex)
      {
         classAlreadyExists = false;
      }
      return classAlreadyExists;
   }

   @SuppressWarnings("unchecked")
   private SOURCETYPE buildJavaSource(JavaSourceFacet java)
   {
      if (named.getValue() == null)
      {
         return null;
      }

      SOURCETYPE source = (SOURCETYPE) Roaster.create(getSourceType()).setName(named.getValue());

      if (targetPackage.hasValue() || targetPackage.hasDefaultValue())
      {
         source.setPackage(targetPackage.getValue());
      }
      else
      {
         source.setPackage(java.getBasePackage());
      }
      return source;
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      Project project = getSelectedProject(validator);
      final JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      if (classExists(javaSourceFacet) && overwrite.isEnabled() && overwrite.getValue() == false)
      {
         validator.addValidationError(named, getType() + " " + targetPackage.getValue() + "." + named.getValue()
                  + " already exists. Use the --overwrite flag to allow the overwrite.");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      SOURCETYPE source = buildJavaSource(javaSourceFacet);
      JavaResource javaResource;
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
         SOURCETYPE decorated = decorateSource(context, project, source);
         if (decorated != null)
            source = decorated;
         javaResource = javaSourceFacet.saveJavaSource(source);
      }

      uiContext.setSelection(javaResource);
      return Results.success(getType() + " " + source.getQualifiedName() + " was created");
   }

   /**
    * Override this method to do any necessary work to customize the generated {@link JavaResource}.
    */
   public SOURCETYPE decorateSource(UIExecutionContext context, Project project, SOURCETYPE source)
            throws Exception
   {
      return source;
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

   protected UIInput<Boolean> getOverwrite()
   {
      return overwrite;
   }

   protected String calculateDefaultPackage(UIContext context)
   {
      String packageName;
      Project project = getSelectedProject(context);
      if (project != null)
      {
         packageName = project.getFacet(JavaSourceFacet.class).getBasePackage();
      }
      else
      {
         packageName = null;
      }
      return packageName;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
