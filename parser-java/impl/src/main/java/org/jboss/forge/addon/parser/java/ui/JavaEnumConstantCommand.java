/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaEnum;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JavaEnumConstantCommand extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(label = "Enum Class", required = true)
   private UISelectOne<JavaResource> enumClass;

   @Inject
   @WithAttributes(label = "Enum Constants", required = true)
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      // Project project = getSelectedProject(context);
      UISelection<Resource<?>> initialSelection = context.getInitialSelection();
      Resource<?> resource = initialSelection.get();
      if (resource instanceof JavaResource)
      {
         enumClass.setDefaultValue((JavaResource) resource);
      }
      builder.add(enumClass).add(arguments);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New Enum Const")
               .description("Creates a new Java Enum constant")
               .category(Categories.create("Java"));
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         if (enumClass.hasValue() && !(enumClass.getValue().getJavaSource() instanceof JavaEnum))
         {
            validator.addValidationError(enumClass, "Enum class must be a valid enum");
         }
      }
      catch (FileNotFoundException e)
      {
         validator.addValidationError(enumClass, "Enum specified not found");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource resource = enumClass.getValue();
      JavaEnum source = (JavaEnum) resource.getJavaSource();
      Project project = getSelectedProject(context.getUIContext());
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      for (String enumConstant : arguments.getValue())
      {
         source.addEnumConstant(enumConstant);
      }
      facet.saveJavaSource(source);
      return Results.success();
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
