/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.EnumSet;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.jboss.forge.addon.javaee.jpa.JPAFieldOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class JPANewFieldRelationshipWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Fetch Type", description = "Whether the association should be lazily loaded or must be eagerly fetched", type = InputType.RADIO)
   private UISelectOne<FetchType> fetchType;

   @Inject
   @WithAttributes(label = "Inverse Field Name", description = "Create a bi-directional relationship, using this value as the name of the inverse field.")
   private UIInput<String> inverseFieldName;

   @Inject
   @WithAttributes(label = "Required", description = "Is this field required ?", defaultValue = "false")
   private UIInput<Boolean> required;

   @Inject
   @WithAttributes(label = "Cascade Type", description = "Define the set of operations that are cascaded to the target.")
   private UISelectMany<CascadeType> cascadeType;

   @Inject
   private JPAFieldOperations persistenceOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Map<Object, Object> attributeMap = context.getAttributeMap();
      RelationshipType relationship = RelationshipType.valueOf(attributeMap.get(RelationshipType.class).toString());
      cascadeType.setValueChoices(EnumSet.range(CascadeType.PERSIST, CascadeType.DETACH));
      boolean shouldAddRequired = false;
      switch (relationship)
      {
      case MANY_TO_MANY:
      case ONE_TO_MANY:
         fetchType.setDefaultValue(FetchType.LAZY);
         break;
      case MANY_TO_ONE:
      case ONE_TO_ONE:
         fetchType.setDefaultValue(FetchType.EAGER);
         shouldAddRequired = true;
         break;
      case EMBEDDED:
         break;
      default:
         throw new UnsupportedOperationException("Relationship " + relationship + " is not supported");
      }
      builder.add(fetchType).add(inverseFieldName);
      if (shouldAddRequired)
      {
         builder.add(required);
      }
      builder.add(cascadeType);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();

      JavaResource entity = (JavaResource) attributeMap.get(JavaResource.class);
      String fieldName = (String) attributeMap.get("fieldName");
      String fieldType = (String) attributeMap.get("fieldType");

      Project project = getSelectedProject(context);
      RelationshipType relationship = RelationshipType.valueOf(attributeMap.get(RelationshipType.class).toString());
      switch (relationship)
      {
      case MANY_TO_MANY:
         persistenceOperations.newManyToManyRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), cascadeType.getValue());
         break;
      case MANY_TO_ONE:
         persistenceOperations.newManyToOneRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
         break;
      case ONE_TO_MANY:
         persistenceOperations.newOneToManyRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), cascadeType.getValue());
         break;
      case ONE_TO_ONE:
         persistenceOperations.newOneToOneRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
         break;
      case EMBEDDED:
         persistenceOperations.newEmbeddedRelationship(project, entity, fieldName, fieldType);
         break;
      default:
         throw new UnsupportedOperationException("Relationship " + relationship + " is not supported");
      }
      return Results.success("Relationship " + relationship.getDescription() + " created");
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: New Field Relationship")
               .description("Setup the relationship for this field")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      // This is the last step
      return null;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}
