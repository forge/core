/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections.ui;

import java.util.Arrays;

import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class CreateConnectionProfileCommandImpl extends AbstractConnectionProfileDetailsPage implements
         CreateConnectionProfileCommand
{
   private UIInput<String> name;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Connection: Create Profile")
               .description("Command to create a database connection profile.")
               .category(Categories.create("Database", "Connections"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      name = factory.createInput("name", String.class).setLabel("Connection Name")
               .setDescription("The name you want to give to this database connection.").setRequired(true);

      builder.add(name);
      super.initializeUI(builder);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ConnectionProfileManagerProvider provider = SimpleContainer
               .getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      ConnectionProfileManager manager = provider.getConnectionProfileManager();
      manager.loadConnectionProfiles();
      ConnectionProfile connectionProfile = new ConnectionProfile();
      connectionProfile.setName(name.getValue());
      connectionProfile.setDialect(hibernateDialect.getValue().getClassName());
      connectionProfile.setDriver(driverClass.getValue().getName());
      connectionProfile.setPath(driverLocation.getValue().getFullyQualifiedName());
      connectionProfile.setUrl(jdbcUrl.getValue());
      connectionProfile.setUser(userName.getValue());
      connectionProfile.setSavePassword(saveUserPassword.getValue());
      connectionProfile.setPassword(userPassword.getValue());
      manager.saveConnectionProfiles(Arrays.asList(connectionProfile));
      return Results.success("Connection profile " + connectionProfile.getName() + " has been saved successfully");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

}
