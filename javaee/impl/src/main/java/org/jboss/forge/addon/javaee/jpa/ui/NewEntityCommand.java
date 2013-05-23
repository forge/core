/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputTypes;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewEntityCommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Entity name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package")
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "ID Column Generation Strategy", required = true)
   private UISelectOne<GenerationType> idStrategy;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("JPA: New Entity").description("Create a new JPA Entity");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      targetPackage.getFacet(HintsFacet.class).setInputType(InputTypes.JAVA_PACKAGE_PICKER);
      idStrategy.setDefaultValue(GenerationType.AUTO);
      builder.add(named).add(targetPackage).add(idStrategy);
      if (getSelectedProject(builder.getUIContext()) == null)
      {
         UISelection<Resource<?>> currentSelection = builder.getUIContext().getInitialSelection();
         if (currentSelection != null)
         {
            Resource<?> resource = currentSelection.get();
            if (resource instanceof DirectoryResource)
            {
               targetLocation.setDefaultValue((DirectoryResource) resource);
            }
            else if (resource instanceof FileResource)
            {
               targetLocation.setDefaultValue(((FileResource<?>) resource).getParent());
            }
         }
         builder.add(targetLocation);
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      String entityName = named.getValue();
      String entityPackage = targetPackage.getValue();
      GenerationType idStrategyChosen = idStrategy.getValue();
      DirectoryResource targetDir = targetLocation.getValue();
      Project project = getSelectedProject(context);
      JavaResource javaResource;
      if (project == null)
      {
         javaResource = persistenceOperations.newEntity(targetDir, entityName, entityPackage, idStrategyChosen);
      }
      else
      {
         javaResource = persistenceOperations.newEntity(project, entityName, entityPackage, idStrategyChosen);
      }
      context.setSelection(javaResource);
      return Results.success("Entity " + javaResource + " created");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      UISelection<Resource<?>> initialSelection = context.getInitialSelection();
      Resource<?> resource = initialSelection.get();
      Project project = null;
      if (resource instanceof DirectoryResource)
      {
         project = projectFactory.findProject((DirectoryResource) resource);
      }
      return project;
   }

}
