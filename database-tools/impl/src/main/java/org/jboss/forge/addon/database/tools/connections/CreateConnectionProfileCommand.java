package org.jboss.forge.addon.database.tools.connections;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class CreateConnectionProfileCommand extends ConnectionProfileDetailsPage implements UICommand
{
   
   private static final String[] COMMAND_CATEGORY = { "Database", "Connections" };
   private static final String COMMAND_NAME = "Connection Profile: Create";
   private static final String COMMAND_DESCRIPTION = "Command to create a database connectin profile.";

   @Inject
   private ConnectionProfileManagerProvider provider;

   @Inject
   @WithAttributes(
            label = "Connection Name",
            description = "The name you want to give to this database connection.",
            required = true)
   private UIInput<String> name;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata
               .forCommand(getClass())
               .name(COMMAND_NAME)
               .description(COMMAND_DESCRIPTION)
               .category(Categories.create(COMMAND_CATEGORY));
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
      Map<String, ConnectionProfile> connectionProfiles =
               provider.getConnectionProfileManager().loadConnectionProfiles();
      ConnectionProfile connectionProfile = new ConnectionProfile();
      connectionProfile.name = name.getValue();
      connectionProfile.dialect = hibernateDialect.getValue().getClassName();
      connectionProfile.driver = driverClass.getValue();
      connectionProfile.path = driverLocation.getValue().getFullyQualifiedName();
      connectionProfile.url = jdbcUrl.getValue();
      connectionProfile.user = userName.getValue();
      connectionProfiles.put(name.getValue(), connectionProfile);
      provider.getConnectionProfileManager().saveConnectionProfiles(connectionProfiles.values());
      return Results.success(
               "Connection profile " +
                        connectionProfile.name +
                        " has been saved succesfully");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

}
