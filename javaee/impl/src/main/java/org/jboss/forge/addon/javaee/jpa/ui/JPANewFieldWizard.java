/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.beans.FieldOperations;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIRegionBuilder;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Creates a new JPA Field
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JavaSourceFacet.class)
public class JPANewFieldWizard extends AbstractJavaEECommand implements UIWizard, PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Target Entity", description = "The targetEntity which the field will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetEntity;

   @Inject
   @WithAttributes(label = "Field Name", description = "The field name to be created in the target targetEntity", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(name = "not-nullable", label = "Not Nullable")
   private UIInput<Boolean> notNullable;

   @Inject
   @WithAttributes(name = "not-updatable", label = "Not Updatable")
   private UIInput<Boolean> notUpdatable;

   @Inject
   @WithAttributes(name = "not-insertable", label = "Not Insertable")
   private UIInput<Boolean> notInsertable;

   @Inject
   @WithAttributes(label = "Field Type", description = "The type intended to be used for this field", type = InputType.JAVA_CLASS_PICKER, required = true, defaultValue = "String")
   private UIInput<String> type;

   @Inject
   @WithAttributes(label = "Relationship Type", description = "The relationship type", type = InputType.RADIO)
   private UISelectOne<RelationshipType> relationshipType;

   @Inject
   @WithAttributes(label = "Is LOB?", description = "If the relationship is a LOB, in this case, it will ignore the value in the Type field", defaultValue = "false")
   private UIInput<Boolean> lob;

   @Inject
   @WithAttributes(label = "Length", defaultValue = "255", description = "The column length. (Applies only if a string-valued column is used.)")
   private UIInput<Integer> length;

   @Inject
   @WithAttributes(label = "Temporal Type", defaultValue = "DATE", description = "Adds @Temporal only if field is java.util.Date or java.util.Calendar", type = InputType.RADIO, enabled = false)
   private UISelectOne<TemporalType> temporalType;

   @Inject
   @WithAttributes(label = "Column Name", description = "The column name. Defaults to the field name")
   private UIInput<String> columnName;

   @Inject
   @WithAttributes(label = "Enum Type", defaultValue = "ORDINAL", description = "Defines mapping for enumerated type. Will be ignored if the type of field is other than enum.", type = InputType.RADIO)
   private UISelectOne<EnumType> enumType;

   @Inject
   @WithAttributes(name = "transient", shortName = 't', label = "Is Transient?", description = "Creates a field with @Transient", defaultValue = "false")
   private UIInput<Boolean> transientField;

   @Inject
   private FieldOperations beanOperations;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Inject
   private ProjectOperations projectOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: New Field")
               .description("Create a new field")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      final Project project = getSelectedProject(builder);
      setupEntities(builder.getUIContext());
      setupRelationshipType();
      final List<String> types = Arrays.asList("byte", "float", "char", "double", "int", "long", "short", "boolean",
               "String", "java.util.Date");
      type.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final List<String> options = new ArrayList<>();
            for (String type : types)
            {
               if (Strings.isNullOrEmpty(value) || type.startsWith(value))
               {
                  options.add(type);
               }
            }
            if (project != null)
            {
               for (JavaResource resource : persistenceOperations.getProjectEntities(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     if (Strings.isNullOrEmpty(value) || qualifiedName.startsWith(value))
                     {
                        options.add(qualifiedName);
                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }
               for (JavaResource resource : projectOperations.getProjectEnums(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     if (Strings.isNullOrEmpty(value) || qualifiedName.startsWith(value))
                     {
                        options.add(qualifiedName);
                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }

            }
            return options;
         }
      });

      relationshipType.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !types.contains(type.getValue()) && !transientField.getValue();
         }
      });

      relationshipType.setValueChoices(new Callable<Iterable<RelationshipType>>()
      {
         @Override
         public Iterable<RelationshipType> call() throws Exception
         {
            final List<RelationshipType> options = new ArrayList<>();
            if (project != null)
            {
               for (JavaResource resource : persistenceOperations.getProjectEntities(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     String simpleName = javaSource.getName();
                     if (qualifiedName.equals(type.getValue()) || simpleName.equals(type.getValue()))
                     {
                        if (javaSource.hasAnnotation(Embeddable.class))
                        {
                           options.add(RelationshipType.EMBEDDED);
                        }
                        else
                        {
                           options.add(RelationshipType.BASIC);
                           options.add(RelationshipType.ONE_TO_MANY);
                           options.add(RelationshipType.ONE_TO_ONE);
                           options.add(RelationshipType.MANY_TO_MANY);
                           options.add(RelationshipType.MANY_TO_ONE);
                        }

                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }
            }
            if (options.isEmpty())
            {
               for (RelationshipType type : RelationshipType.values())
               {
                  options.add(type);
               }
            }
            return options;
         }
      });

      lob.setEnabled(() -> relationshipType.getValue() == RelationshipType.BASIC && !transientField.getValue());
      type.setEnabled(() -> !lob.getValue() && !transientField.getValue());
      columnName.setEnabled(() -> !transientField.getValue());
      notNullable.setEnabled(() -> !transientField.getValue());
      notInsertable.setEnabled(() -> !transientField.getValue());
      notUpdatable.setEnabled(() -> !transientField.getValue());
      length.setEnabled(() -> !lob.getValue() && !transientField.getValue());
      temporalType.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            String typeValue = type.getValue();
            return !transientField.getValue()
                     && (Date.class.getName().equals(typeValue) || Calendar.class.getName().equals(typeValue));
         }
      });

      enumType.setEnabled(new Callable<Boolean>()
      {

         @Override
         public Boolean call() throws Exception
         {
            JavaClassSource targetEntityType = null;
            if (targetEntity.getValue() != null)
            {
               try
               {
                  targetEntityType = targetEntity.getValue().getJavaType();
               }
               catch (FileNotFoundException | ResourceException ignored)
               {
               }
            }

            return !lob.getValue() && !transientField.getValue()
                     && beanOperations.isFieldTypeEnum(project, targetEntityType, type.getValue());
         }
      });
      type.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));
      builder.add(targetEntity).add(named).add(type).add(temporalType).add(columnName).add(length)
               .add(notNullable).add(notInsertable).add(notUpdatable)
               .add(relationshipType)
               .add(lob).add(transientField)
               .add(enumType);
   }

   private void setupEntities(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = persistenceOperations.getProjectEntities(project);
      targetEntity.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx != -1)
      {
         targetEntity.setDefaultValue(entities.get(idx));
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
   public Result execute(UIExecutionContext context) throws Exception
   {
      Project project = getSelectedProject(context);
      JavaResource javaResource = targetEntity.getValue();
      String fieldNameStr = named.getValue();
      JavaClassSource targetEntity = javaResource.getJavaType();
      FieldSource<JavaClassSource> field = targetEntity.getField(fieldNameStr);
      String action = (field == null) ? "created" : "updated";
      if (field != null)
      {
         UIPrompt prompt = context.getPrompt();
         if (prompt.promptBoolean("Field '" + field.getName() + "' already exists. Do you want to overwrite it?"))
         {
            beanOperations.removeField(targetEntity, field);
         }
         else
         {
            return Results.fail("Field '" + field.getName() + "' already exists.");
         }
      }
      RelationshipType value = (relationshipType.isEnabled()) ? relationshipType.getValue() : RelationshipType.BASIC;
      if (transientField.getValue())
      {
         String fieldType = type.getValue();
         field = beanOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                  Transient.class.getCanonicalName());
         setCurrentWorkingResource(context, javaResource, field);
         return Results.success("Transient Field " + named.getValue() + " " + action);
      }
      else if (value == RelationshipType.BASIC)
      {
         if (lob.getValue())
         {
            String fieldType = type.getValue();
            field = beanOperations.addFieldTo(targetEntity, fieldType, fieldNameStr, Lob.class.getName());
            field.addAnnotation(Column.class).setLiteralValue("length", String.valueOf(Integer.MAX_VALUE));
         }
         else if (beanOperations.isFieldTypeEnum(project, targetEntity, type.getValue()))
         {
            String fieldType = type.getValue();

            field = beanOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                     Enumerated.class.getCanonicalName());

            if (enumType.isEnabled() && enumType.getValue() != EnumType.ORDINAL)
            {
               field.getAnnotation(Enumerated.class).setEnumArrayValue(enumType.getValue());
            }

            if (columnName.isEnabled() && columnName.hasValue())
            {
               field.addAnnotation(Column.class).setStringValue("name", columnName.getValue());
            }

            if (notNullable.isEnabled() && notNullable.getValue())
            {
               setLiteralValueInColumn(field, "nullable", "false");
            }
            if (notInsertable.isEnabled() && notInsertable.getValue())
            {
               setLiteralValueInColumn(field, "insertable", "false");
            }
            if (notUpdatable.isEnabled() && notUpdatable.getValue())
            {
               setLiteralValueInColumn(field, "updatable", "false");
            }
         }
         else
         {
            String fieldType = type.getValue();
            field = beanOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                     Column.class.getCanonicalName());
         }
         if (length.isEnabled() && length.getValue() != null && length.getValue().intValue() != 255)
         {
            setLiteralValueInColumn(field, "length", String.valueOf(length.getValue()));
         }
         if (columnName.isEnabled() && columnName.hasValue())
         {
            field.getAnnotation(Column.class).setStringValue("name", columnName.getValue());
         }
         if (notNullable.isEnabled() && notNullable.getValue())
         {
            setLiteralValueInColumn(field, "nullable", "false");
         }
         if (notInsertable.isEnabled() && notInsertable.getValue())
         {
            setLiteralValueInColumn(field, "insertable", "false");
         }
         if (notUpdatable.isEnabled() && notUpdatable.getValue())
         {
            setLiteralValueInColumn(field, "updatable", "false");
         }
         if (temporalType.isEnabled())
         {
            field.addAnnotation(Temporal.class).setEnumValue(temporalType.getValue());
         }
         setCurrentWorkingResource(context, javaResource, field);
         return Results.success("Field " + named.getValue() + " " + action);
      }
      else
      {
         // Field creation will occur in NewFieldRelationshipWizardStep
         return Results.success();
      }
   }

   private void setLiteralValueInColumn(FieldSource<JavaClassSource> field, String literal, String value)
   {
      if (field.getAnnotation(Column.class) != null)
      {
         field.getAnnotation(Column.class).setLiteralValue(literal, value);
      }
   }

   /**
    * @param context
    * @param javaResource
    * @param field
    * @throws FileNotFoundException
    */
   private void setCurrentWorkingResource(UIExecutionContext context, JavaResource javaResource,
            FieldSource<JavaClassSource> field)
            throws FileNotFoundException
   {
      Project selectedProject = getSelectedProject(context);
      if (selectedProject != null)
      {
         JavaSourceFacet facet = selectedProject.getFacet(JavaSourceFacet.class);
         facet.saveJavaSource(field.getOrigin());
      }
      // For some reason the field start/end position is not set. Investigate.
      JavaClassSource source = javaResource.getJavaType();
      final FieldSource<JavaClassSource> fieldWithPosition = source.getField(field.getName());
      UISelection<JavaResource> selection = Selections
               .from(resource -> UIRegionBuilder.create(resource)
                        .startLine(fieldWithPosition.getLineNumber())
                        .endLine(fieldWithPosition.getLineNumber())
                        .startPosition(fieldWithPosition.getStartPosition())
                        .endPosition(fieldWithPosition.getEndPosition()),
                        Collections.singleton(javaResource));
      context.getUIContext().setSelection(selection);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         JavaResource javaResource = targetEntity.getValue();
         if (javaResource != null)
         {
            JavaClassSource javaClass = javaResource.getJavaType();
            if (javaClass.hasField(named.getValue()))
            {
               validator.addValidationWarning(targetEntity, "Field '" + named.getValue() + "' already exists");
            }
         }
      }
      catch (FileNotFoundException ffe)
      {
         validator.addValidationError(targetEntity, "Entity could not be found");
      }

      if (length.isEnabled())
      {
         if (length.getValue() == null || length.getValue() <= 0)
         {
            validator.addValidationError(length, "Length should be a positive integer");
         }
      }
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      attributeMap.put(JavaResource.class, targetEntity.getValue());
      attributeMap.put("fieldName", named.getValue());
      attributeMap.put("fieldType", type.getValue());
      attributeMap.put(RelationshipType.class, relationshipType.getValue());
      if (relationshipType.getValue() == RelationshipType.BASIC)
      {
         return null;
      }
      else
      {
         return Results.navigateTo(JPANewFieldRelationshipWizardStep.class);
      }
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
         if (!project.hasFacet(JPAFacet.class))
         {
            builder.add(JPASetupWizard.class);
         }
      }
      return builder.build();
   }

}
