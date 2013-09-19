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
import org.jboss.forge.addon.javaee.jpa.FieldOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
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

public class NewFieldWizard extends AbstractJavaEECommand implements UIWizard
{
   @Inject
   @WithAttributes(label = "Entity", description = "The entity which the field will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> entity;

   @Inject
   @WithAttributes(label = "Field Name", description = "The field name to be created in the target entity", required = true)
   private UIInput<String> fieldName;

   @Inject
   @WithAttributes(label = "Type", description = "The type intended to be used for this field", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> typeName;

   @Inject
   @WithAttributes(label = "Relationship", description = "The type of the relationship", type = InputType.RADIO)
   private UISelectOne<RelationshipType> relationshipType;

   @Inject
   @WithAttributes(label = "Use Primitive Version?", description = "For this field type, use the primitive version", defaultValue = "false")
   private UIInput<Boolean> primitive;

   @Inject
   @WithAttributes(label = "Is LOB?", description = "If the relationship is a LOB, in this case, it will ignore the value in the Type field", defaultValue = "false")
   private UIInput<Boolean> lob;

   @Inject
   @WithAttributes(label = "Length", defaultValue = "255", description = "The column length. (Applies only if a string-valued column is used.)")
   private UIInput<Integer> length;

   @Inject
   private FieldOperations fieldOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: New Field").description("Create a new field")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupEntities(builder.getUIContext());
      setupRelationshipType();
      lob.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !primitive.getValue() && relationshipType.getValue() == RelationshipType.BASIC;
         }
      });
      primitive.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return relationshipType.getValue() == RelationshipType.BASIC;
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
      length.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !lob.getValue();
         }
      });
      builder.add(entity).add(fieldName).add(typeName).add(length).add(relationshipType).add(lob).add(primitive);
   }

   private void setupEntities(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
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
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx == -1)
      {
         idx = entities.size() - 1;
      }
      if (idx != -1)
      {
         entity.setDefaultValue(entities.get(idx));
      }
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
      RelationshipType value = relationshipType.getValue();
      if (value == RelationshipType.BASIC)
      {
         if (primitive.getValue())
         {
            String fieldType = getPrimitiveTypeFor(typeName.getValue());
            field = fieldOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                     Column.class.getCanonicalName());
         }
         else if (lob.getValue())
         {
            String fieldType = byte[].class.getName();
            field = fieldOperations.addFieldTo(targetEntity, fieldType, fieldNameStr, Lob.class.getName());
            field.addAnnotation(Column.class).setLiteralValue("length", String.valueOf(Integer.MAX_VALUE));
         }
         else
         {
            String fieldType = typeName.getValue();
            field = fieldOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                     Column.class.getCanonicalName());
         }
         if (length.isEnabled() && length.getValue() != null && length.getValue().intValue() != 255)
         {
            field.getAnnotation(Column.class).setLiteralValue("length", String.valueOf(length.getValue()));
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
      else
      {
         // Field creation will occur in NewFieldRelationshipWizardStep
         return Results.success();
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         JavaResource javaResource = entity.getValue();
         JavaClass javaClass = (JavaClass) javaResource.getJavaSource();
         if (javaClass.hasField(fieldName.getValue()))
         {
            validator.addValidationError(entity, "Field '" + fieldName.getValue() + "' already exists");
         }
      }
      catch (FileNotFoundException ffe)
      {
         validator.addValidationError(entity, "Entity could not be found");
      }
      if (primitive.getValue())
      {
         String primitiveType = getPrimitiveTypeFor(typeName.getValue());
         if (primitiveType == null)
         {
            validator.addValidationError(typeName, "Type is not a wrapper of a primitive type");
         }
      }

      if (length.isEnabled())
      {
         if (length.getValue() == null || length.getValue() <= 0)
         {
            validator.addValidationError(length, "Length should be a positive integer");
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
      context.setAttribute(JavaResource.class, entity.getValue());
      context.setAttribute("fieldName", fieldName.getValue());
      context.setAttribute("fieldType", typeName.getValue());
      context.setAttribute(RelationshipType.class, relationshipType.getValue());
      context.getAttribute(RelationshipType.class);
      if (relationshipType.getValue() == RelationshipType.BASIC)
      {
         return null;
      }
      else
      {
         return Results.navigateTo(NewFieldRelationshipWizardStep.class);
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}