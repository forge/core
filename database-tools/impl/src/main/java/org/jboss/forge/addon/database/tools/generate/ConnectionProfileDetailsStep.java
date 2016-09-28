/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.generate;

import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.util.HibernateToolsHelper;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ConnectionProfileDetailsStep extends AbstractConnectionProfileDetailsPage implements UIWizardStep
{
   private final GenerateEntitiesCommandDescriptor descriptor;
   private boolean initialized;

   public ConnectionProfileDetailsStep(GenerateEntitiesCommandDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      if (initialized)
      {
         initializeBuilder(builder);
      }
      else
      {
         super.initializeUI(builder);
         this.initialized = true;
      }
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Connection Profile Details")
               .description("Edit the connection profile details");
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext context)
   {
      FileResource<?> driverLocationResource = driverLocation.getValue();
      if (driverLocationResource != null)
      {
         descriptor.setUrls(HibernateToolsHelper.getDriverUrls(driverLocationResource));
      }
      Class<?> driverClassValue = driverClass.getValue();
      if (driverClassValue != null)
      {
         descriptor.setDriverClass(driverClassValue.getName());
      }
      descriptor.setConnectionProperties(createConnectionProperties());
      super.validate(context);
   }
}
