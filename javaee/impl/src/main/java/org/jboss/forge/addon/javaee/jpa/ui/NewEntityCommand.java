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
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewEntityCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Entity name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "ID Column Generation Strategy", required = true)
   private UISelectOne<GenerationType> idStrategy;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Override
   public Metadata getMetadata()
   {
      return Metadata.from(super.getMetadata(), getClass()).name("JPA: New Entity")
               .description("Create a new JPA Entity")
               .category(Categories.create(super.getMetadata().getCategory(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      idStrategy.setDefaultValue(GenerationType.AUTO);
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
         targetLocation.setDefaultValue(facet.getSourceFolder()).setEnabled(false);
      }
      builder.add(targetLocation);
      builder.add(targetPackage).add(named).add(idStrategy);
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
   protected boolean isProjectRequired()
   {
      return false;
   }
}
