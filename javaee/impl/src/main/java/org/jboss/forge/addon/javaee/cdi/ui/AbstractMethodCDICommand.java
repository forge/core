/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Martin Kouba
 */
public abstract class AbstractMethodCDICommand extends AbstractJavaEECommand implements PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Target Class", description = "The class where the method will be created", required = true, type = InputType.DROPDOWN)
   protected UISelectOne<JavaResource> targetClass;

   @Inject
   @WithAttributes(label = "Method Name", description = "The name of the created method", required = true)
   protected UIInput<String> named;

   @Inject
   @WithAttributes(label = "Access Type", description = "The access type", type = InputType.RADIO, defaultValue = "PRIVATE")
   protected UISelectOne<Visibility> accessType;

   @Inject
   private ProjectOperations projectOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupTargetClass(builder.getUIContext());
      setupAccessType();
      builder.add(targetClass).add(named).add(accessType);
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .category(Categories.create(Categories.create("Java EE"), "CDI"));
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      JavaResource javaResource = targetClass.getValue();
      if (javaResource != null && javaResource.exists())
      {
         JavaClassSource javaClass;
         try
         {
            javaClass = javaResource.getJavaType();
            if (javaClass.hasMethodSignature(named.getValue(), getParamTypes()))
            {
               validator.addValidationError(named, "Method signature already exists");
            }
         }
         catch (FileNotFoundException ignored)
         {
         }
      }
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(CDIFacet.class))
         {
            builder.add(CDISetupCommand.class);
         }
      }
      return builder.build();
   }


   protected void setupTargetClass(UIContext uiContext)
   {
      Project project = getSelectedProject(uiContext);
      UISelection<Resource<?>> resource = uiContext.getInitialSelection();
      if (resource.get() instanceof JavaResource)
      {
         targetClass.setDefaultValue((JavaResource) resource.get());
      }
      targetClass.setValueChoices(projectOperations.getProjectClasses(project));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   protected Visibility getDefaultVisibility()
   {
      return Visibility.PUBLIC;
   }

   protected String[] getParamTypes() {
      return new String[] {};
   }

   private void setupAccessType()
   {
      accessType.setItemLabelConverter(new Converter<Visibility, String>()
      {
         @Override
         public String convert(Visibility source)
         {
            if (source == null)
               return null;
            if (source == Visibility.PACKAGE_PRIVATE)
            {
               return "default";
            }
            return source.toString();
         }
      });
      accessType.setDefaultValue(getDefaultVisibility());
   }
}
