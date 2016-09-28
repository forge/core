/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.generate;

import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ConnectionProfileDetailsStep extends AbstractConnectionProfileDetailsPage implements UIWizardStep
{
   private GenerateEntitiesCommandDescriptor descriptor;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Connection Profile Details")
               .description("Edit the connection profile details");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      descriptor = (GenerateEntitiesCommandDescriptor) builder.getUIContext().getAttributeMap()
               .get(GenerateEntitiesCommandDescriptor.class);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return Results.navigateTo(DatabaseTableSelectionStep.class);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (driverLocation.getValue() != null)
      {
         descriptor.setUrls(HibernateToolsHelper.getDriverUrls(driverLocation.getValue()));
      }
      if (driverClass.getValue() != null)
      {
         descriptor.setDriverClass(driverClass.getValue().getName());
      }
      descriptor.setConnectionProperties(createConnectionProperties());
   }

}
