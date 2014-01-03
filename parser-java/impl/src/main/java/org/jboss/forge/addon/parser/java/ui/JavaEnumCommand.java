/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import java.io.PrintStream;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.SyntaxError;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEnumCommand extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(label = "Package Name", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> packageName;

   @Inject
   @WithAttributes(label = "Enum Name", required = true)
   private UIInput<String> named;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(packageName).add(named);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New Enum").description("Creates a new Java Enum")
               .category(Categories.create("Java"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      JavaEnum enumType = JavaParser.create(JavaEnum.class).setName(named.getValue());
      JavaResource javaResource;
      if (packageName.hasValue())
      {
         enumType.setPackage(packageName.getValue());
      }
      else
      {
         enumType.setPackage(javaSourceFacet.getBasePackage());
      }
      if (enumType.hasSyntaxErrors())
      {
         UIOutput output = uiContext.getProvider().getOutput();
         PrintStream err = output.err();
         err.println("Syntax Errors:");
         for (SyntaxError error : enumType.getSyntaxErrors())
         {
            err.println(error);
         }
         err.println();
         return Results.fail("Syntax Errors found. See above");
      }
      else
      {
         javaResource = javaSourceFacet.saveJavaSource(enumType);
      }
      uiContext.setSelection(javaResource);
      return Results.success("Enum " + enumType.getName() + " was created");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
