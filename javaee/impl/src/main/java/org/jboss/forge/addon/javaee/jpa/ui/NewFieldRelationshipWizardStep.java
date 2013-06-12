/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.jboss.forge.addon.javaee.ui.AbstractProjectUICommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
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
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;

public class NewFieldRelationshipWizardStep extends AbstractProjectUICommand implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Fetch Type", description = "Whether the association should be lazily loaded or must be eagerly fetched", required = true, type = InputType.SELECT_ONE_RADIO)
   private UISelectOne<FetchType> fetchType;

   @Inject
   @WithAttributes(label = "Inverse Field Name", description = "Create a bi-directional relationship, using this value as the name of the inverse field.")
   private UIInput<String> inverseFieldName;

   @Inject
   @WithAttributes(label = "Cascade Type", description = "Define the set of operations that are cascaded to the target.", required = true, type = InputType.SELECT_ONE_DROPDOWN)
   private UISelectOne<CascadeType> cascadeType;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(fetchType).add(inverseFieldName).add(cascadeType);
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Field<JavaClass> field = (Field<JavaClass>) context.getAttribute(Field.class);
      RelationshipType relationship = (RelationshipType) context.getAttribute(RelationshipType.class);
      JavaResource resource = (JavaResource) context.getAttribute(JavaResource.class);
      switch (relationship)
      {
      case MANY_TO_MANY:
         break;
      case MANY_TO_ONE:
         break;
      case ONE_TO_MANY:
         break;
      case ONE_TO_ONE:
         break;
      default:
         throw new UnsupportedOperationException("Relationship " + relationship + " is not supported");
      }
      return Results.success();
   }

   @Override
   public Metadata getMetadata()
   {
      Metadata metadata = super.getMetadata();
      return metadata.name("JPA: New Field Relationship").description("Setup the relationship for this field")
               .category(Categories.create(metadata.getCategory().getName(), "JPA"));
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      // This is the last step
      return null;
   }
}
