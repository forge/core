/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ui.AbstractProjectUICommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class NewFieldCommand extends AbstractProjectUICommand implements UIWizard
{

   @Inject
   @WithAttributes(label = "Entity", description = "The entity which the field will be created", required = true, type = InputType.SELECT_ONE_DROPDOWN)
   private UISelectOne<JavaResource> entity;

   @Inject
   @WithAttributes(label = "Field Name", description = "The field name to be created in the target entity", required = true)
   private UIInput<String> fieldName;

   @Inject
   @WithAttributes(label = "Type", description = "The type intended to be used for this field", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> typeName;

   @Inject
   @WithAttributes(label = "Relationship", description = "The type of the relationship", type = InputType.SELECT_ONE_RADIO)
   private UISelectOne<RelationshipType> relationshipType;

   @Override
   public Metadata getMetadata()
   {
      Metadata metadata = super.getMetadata();
      return metadata.name("JPA: New Field").description("Create a new field")
               .category(Categories.create(metadata.getCategory().getName(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      setupEntities(project);
      setupRelationshipType();
      builder.add(entity).add(fieldName).add(typeName).add(relationshipType);
   }

   private void setupEntities(Project project)
   {
      final List<JavaResource> entities = new ArrayList<JavaResource>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {

            @Override
            public void visit(JavaResource resource)
            {
               try
               {
                  if (resource.getJavaSource().hasAnnotation(Entity.class))
                  {
                     entities.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      entity.setValueChoices(entities);
   }

   private void setupRelationshipType()
   {
      relationshipType.setItemLabelConverter(new Converter<RelationshipType, String>()
      {
         @Override
         public String convert(RelationshipType source)
         {
            return (source == null) ? null : source.getDescription();
         }
      });
      relationshipType.setDefaultValue(RelationshipType.BASIC);
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success("Field " + fieldName.getValue() + " created");
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }
}