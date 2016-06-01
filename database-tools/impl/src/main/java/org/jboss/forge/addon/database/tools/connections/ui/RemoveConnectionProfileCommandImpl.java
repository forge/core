/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections.ui;

import java.util.Map;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerProvider;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class RemoveConnectionProfileCommandImpl extends AbstractUICommand implements RemoveConnectionProfileCommand
{

   private static final String[] COMMAND_CATEGORY = { "Database", "Connections" };
   private static final String COMMAND_NAME = "Connection: Remove Profile";
   private static final String COMMAND_DESCRIPTION = "Command to remove a database connection profile.";

   private Map<String, ConnectionProfile> profiles;

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
      InputComponentFactory factory = builder.getInputComponentFactory();
      names = factory.createSelectMany("names", String.class).setLabel("Connection Name")
               .setDescription("The name of the database connection profiles you want to remove.");
      ConnectionProfileManagerProvider provider = SimpleContainer
               .getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      profiles = provider.getConnectionProfileManager().loadConnectionProfiles();
      names.setValueChoices(profiles.keySet());
      builder.add(names);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ConnectionProfileManagerProvider provider = SimpleContainer
               .getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
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
         removedProfiles = removedProfiles.substring(0, lastIndex) +
                  " and" +
                  removedProfiles.substring(lastIndex + 1);
         message += "s " + removedProfiles + " have";
      }
      else
      {
         message += " " + removedProfiles + " has";
      }
      message += " been removed successfully";
      return Results.success(message);
   }

}
