/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.jboss.forge.addon.javaee.jpa.FieldOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
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

public class NewFieldRelationshipWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Fetch Type", description = "Whether the association should be lazily loaded or must be eagerly fetched", required = true, type = InputType.RADIO)
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
   private FieldOperations persistenceOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      RelationshipType relationship = RelationshipType.valueOf(context.getAttribute(RelationshipType.class).toString());
      cascadeType.setValueChoices(EnumSet.range(CascadeType.PERSIST, CascadeType.DETACH));
      switch (relationship)
      {
      case MANY_TO_MANY:
      case ONE_TO_MANY:
         fetchType.setDefaultValue(FetchType.LAZY);
         break;
      case MANY_TO_ONE:
      case ONE_TO_ONE:
         fetchType.setDefaultValue(FetchType.EAGER);
         break;
      default:
         throw new UnsupportedOperationException("Relationship " + relationship + " is not supported");
      }
      builder.add(fetchType).add(inverseFieldName).add(required).add(cascadeType);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      JavaResource entity = (JavaResource) uiContext.getAttribute(JavaResource.class);
      String fieldName = (String) uiContext.getAttribute("fieldName");
      String fieldType = (String) uiContext.getAttribute("fieldType");

      Project project = getSelectedProject(context);
      RelationshipType relationship = RelationshipType.valueOf(uiContext.getAttribute(RelationshipType.class).toString());
      switch (relationship)
      {
      case MANY_TO_MANY:
         persistenceOperations.newManyToManyRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
         break;
      case MANY_TO_ONE:
         persistenceOperations.newManyToOneRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
         break;
      case ONE_TO_MANY:
         persistenceOperations.newOneToManyRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
         break;
      case ONE_TO_ONE:
         persistenceOperations.newOneToOneRelationship(project, entity, fieldName, fieldType,
                  inverseFieldName.getValue(), fetchType.getValue(), required.getValue(), cascadeType.getValue());
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
   public NavigationResult next(UIContext context) throws Exception
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
