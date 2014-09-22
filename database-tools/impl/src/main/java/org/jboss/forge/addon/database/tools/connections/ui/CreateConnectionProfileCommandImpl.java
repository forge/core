package org.jboss.forge.addon.database.tools.connections.ui;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.AbstractConnectionProfileDetailsPage;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class CreateConnectionProfileCommandImpl extends AbstractConnectionProfileDetailsPage implements
         CreateConnectionProfileCommand
{

   @Inject
   private ConnectionProfileManagerProvider provider;

   @Inject
   @WithAttributes(label = "Connection Name", description = "The name you want to give to this database connection.", required = true)
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
      builder.add(name);
      super.initializeUI(builder);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Map<String, ConnectionProfile> connectionProfiles = provider.getConnectionProfileManager()
               .loadConnectionProfiles();
      ConnectionProfile connectionProfile = new ConnectionProfile();
      connectionProfile.setName(name.getValue());
      connectionProfile.setDialect(hibernateDialect.getValue().getClassName());
      connectionProfile.setDriver(driverClass.getValue().getName());
      connectionProfile.setPath(driverLocation.getValue().getFullyQualifiedName());
      connectionProfile.setUrl(jdbcUrl.getValue());
      connectionProfile.setUser(userName.getValue());
      connectionProfile.setSavePassword(saveUserPassword.getValue());
      connectionProfile.setPassword(userPassword.getValue());
      connectionProfiles.put(name.getValue(), connectionProfile);
      provider.getConnectionProfileManager().saveConnectionProfiles(connectionProfiles.values());
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
