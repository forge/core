package org.jboss.forge.addon.database.tools.connections;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class RemoveConnectionProfileCommand extends AbstractUICommand
{

   private static final String[] COMMAND_CATEGORY = { "Database", "Connections" };
   private static final String COMMAND_NAME = "Connection Profile: Remove";
   private static final String COMMAND_DESCRIPTION = "Command to remove a database connectin profile.";

   @Inject
   private ConnectionProfileManagerProvider provider;

   private Map<String, ConnectionProfile> profiles;

   @Inject
   @WithAttributes(
            label = "Connection Name",
            description = "The name of the database connection profiles you want to remove.")
   private UISelectMany<String> names;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name(COMMAND_NAME)
               .description(COMMAND_DESCRIPTION)
               .category(Categories.create(COMMAND_CATEGORY));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      profiles = provider.getConnectionProfileManager().loadConnectionProfiles();
      names.setValueChoices(profiles.keySet());
      builder.add(names);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Iterable<String> selection = names.getValue();
      StringBuffer sb = new StringBuffer();
      for (String name : selection)
      {
         profiles.remove(name);
         sb.append(name + ", ");
      }
      provider.getConnectionProfileManager().saveConnectionProfiles(profiles.values());
      if (sb.length() > 2)
      {
         sb.setLength(sb.length() - 2);
      }
      String message = "Connection profile";
      String removedProfiles = sb.toString();
      if (removedProfiles.contains(", "))
      {
         int lastIndex = removedProfiles.lastIndexOf(',');
         removedProfiles =
                  removedProfiles.substring(0, lastIndex) +
                           " and" +
                           removedProfiles.substring(lastIndex + 1);
         message += "s " + removedProfiles + " have";
      }
      else
      {
         message += " " + removedProfiles + " has";
      }
      message += " been removed succesfully";
      return Results.success(message);
   }

}
