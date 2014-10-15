/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.ui.CDISetupCommand;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.javaee.faces.FacesOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Creates a new JSF Backing Bean
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class FacesNewBeanCommand extends AbstractFacesCommand implements PrerequisiteCommandsProvider
{

   @Inject
   @WithAttributes(label = "Bean name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private FacesOperations facesOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Faces: New Bean")
               .description("Create a new JSF Backing Bean")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JSF"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      if (project == null)
      {
         UISelection<FileResource<?>> currentSelection = builder.getUIContext().getInitialSelection();
         if (!currentSelection.isEmpty())
         {
            FileResource<?> resource = currentSelection.get();
            if (resource instanceof DirectoryResource)
            {
               targetLocation.setDefaultValue((DirectoryResource) resource);
            }
            else
            {
               targetLocation.setDefaultValue(resource.getParent());
            }
         }
      }
      else if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         targetLocation.setDefaultValue(facet.getSourceDirectory()).setEnabled(false);
         targetPackage.setValue(calculateBackingBeanPackage(project));
      }
      builder.add(targetLocation);
      builder.add(targetPackage).add(named);
   }

   private String calculateBackingBeanPackage(Project project)
   {
      return project.getFacet(JavaSourceFacet.class).getBasePackage() + ".view";
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String beanName = named.getValue();
      String beanPackage = targetPackage.getValue();
      DirectoryResource targetDir = targetLocation.getValue();
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaResource javaResource;
      if (project == null)
      {
         javaResource = facesOperations.newBackingBean(targetDir, beanName, beanPackage);
      }
      else
      {
         javaResource = facesOperations.newBackingBean(project, beanName, beanPackage);
      }
      uiContext.setSelection(javaResource);
      return Results.success("Backing bean " + javaResource + " created");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
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
         if (!project.hasFacet(FacesFacet.class))
         {
            builder.add(FacesSetupWizardImpl.class);
         }
      }
      return builder.build();
   }
}
