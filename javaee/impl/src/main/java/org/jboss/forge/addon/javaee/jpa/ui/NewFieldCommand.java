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
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.ui.AbstractProjectUICommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
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
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Types;

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

   @Inject
   @WithAttributes(label = "Use Primitive Version?", description = "For this field type, use the primitive version")
   private UIInput<Boolean> primitive;

   @Inject
   @WithAttributes(label = "Is LOB?", description = "If the relationship is a LOB, in this case, it will ignore the value in the Type field")
   private UIInput<Boolean> lob;

   @Inject
   private PersistenceOperations persistenceOperations;

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
      lob.setDefaultValue(Boolean.FALSE);
      primitive.setDefaultValue(Boolean.FALSE);
      lob.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !primitive.getValue();
         }
      });
      typeName.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !lob.getValue();
         }
      });
      builder.add(entity).add(fieldName).add(typeName).add(relationshipType).add(lob).add(primitive);
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
      if (!entities.isEmpty())
         entity.setDefaultValue(entities.get(0));
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
      JavaResource javaResource = entity.getValue();
      String fieldNameStr = fieldName.getValue();
      Field<JavaClass> field;
      JavaClass targetEntity = (JavaClass) javaResource.getJavaSource();
      if (primitive.getValue())
      {
         String fieldType = getPrimitiveTypeFor(typeName.getValue());
         field = persistenceOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                  Column.class.getCanonicalName());
      }
      else if (lob.getValue())
      {
         String fieldType = byte[].class.getName();
         field = persistenceOperations.addFieldTo(targetEntity, fieldType, fieldNameStr, Lob.class.getName());
         // TODO: Specify column length somewhere ?
         field.addAnnotation(Column.class).setLiteralValue("length", String.valueOf(Integer.MAX_VALUE));
      }
      else
      {
         String fieldType = typeName.getValue();
         field = persistenceOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                  Column.class.getCanonicalName());
      }
      Project selectedProject = getSelectedProject(context);
      if (selectedProject != null)
      {
         JavaSourceFacet facet = selectedProject.getFacet(JavaSourceFacet.class);
         facet.saveJavaSource(field.getOrigin());
      }
      context.setSelection(javaResource);
      return Results.success("Field " + fieldName.getValue() + " created");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      if (primitive.getValue())
      {
         String primitiveType = getPrimitiveTypeFor(typeName.getValue());
         if (primitiveType == null)
         {
            validator.addValidationError(typeName, "Type is not a wrapper of a primitive type");
         }
      }
   }

   private String getPrimitiveTypeFor(String value)
   {
      if (value == null)
         return null;
      String val = value.toLowerCase().replaceAll("java.lang.", "");
      if (val.equals("integer"))
      {
         val = "int";
      }
      else if (val.equals("character"))
      {
         val = "char";
      }
      return (Types.isPrimitive(val)) ? val : null;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }
}