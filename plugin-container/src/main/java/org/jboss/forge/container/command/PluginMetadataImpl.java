/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.plugin.Plugin;
import org.jboss.forge.plugin.meta.CommandMetadata;
import org.jboss.forge.plugin.meta.PluginMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
public class PluginMetadataImpl implements PluginMetadata
{
   private String help = "";
   private String name = "";
   private String topic = "Other";
   private Class<? extends Plugin> type;

   private final Map<String, List<CommandMetadata>> commandMap = new HashMap<String, List<CommandMetadata>>();

   private CommandMetadata defaultCommand;
   private CommandMetadata setupCommand;

   private boolean scopeOverloaded = false;

   @Override
   public CommandMetadata getCommand(final String name)
   {
      if (commandMap.containsKey(name) && (commandMap.get(name).size() > 1))
      {
         throw new RuntimeException("ambiguous query: overloaded commands exist");
      }
      else if (!commandMap.get(name).isEmpty())
      {
         return commandMap.get(name).iterator().next();
      }
      return null;
   }

   public Map<String, List<CommandMetadata>> getCommandMap()
   {
      return commandMap;
   }

   @Override
   public boolean hasCommand(final String name)
   {
      return getCommand(name) != null;
   }

   @Override
   public boolean hasDefaultCommand()
   {
      return getDefaultCommand() != null;
   }

   @Override
   public boolean hasCommands()
   {
      return !commandMap.isEmpty();
   }

   @Override
   public boolean hasSetupCommand()
   {
      return getSetupCommand() != null;
   }

   @Override
   public CommandMetadata getSetupCommand()
   {
      return setupCommand;
   }

   @Override
   public CommandMetadata getDefaultCommand()
   {
      return defaultCommand;
   }

   public void addCommands(final List<CommandMetadata> commands)
   {
      for (CommandMetadata c : commands)
      {
         addCommand(c);
      }
   }

   public void addCommand(final CommandMetadata command)
   {
      if (command.isDefault())
      {
         if (defaultCommand != null)
         {
            throw new RuntimeException("default command already defined: " + command.getName() + "; for plugin: "
                     + name);
         }
         defaultCommand = command;
      }

      if (command.isSetup())
      {
         if (setupCommand != null)
         {
            throw new RuntimeException("setup command already defined: " + command.getName() + "; for plugin: "
                     + name);
         }
         setupCommand = command;
      }

      if (!commandMap.containsKey(command.getName()))
      {
         commandMap.put(command.getName(), new ArrayList<CommandMetadata>());
      }
      else
      {
         scopeOverloaded = true;
      }

      commandMap.get(command.getName()).add(command);
   }

   @Override
   public List<CommandMetadata> getCommands()
   {
      List<CommandMetadata> result = new ArrayList<CommandMetadata>();
      for (List<CommandMetadata> cl : commandMap.values())
      {
         for (CommandMetadata c : cl)
         {
            result.add(c);
         }
      }
      return Collections.unmodifiableList(result);
   }

   @Override
   public String toString()
   {
      return name;
   }

   @Override
   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   @Override
   public Class<? extends Plugin> getType()
   {
      return type;
   }

   public void setType(final Class<? extends Plugin> type)
   {
      this.type = type;
   }

   @Override
   public String getHelp()
   {
      return help;
   }

   public void setHelp(final String help)
   {
      this.help = help;
   }

   @Override
   public String getTopic()
   {
      return topic;
   }

   public void setTopic(final String topic)
   {
      this.topic = topic.toUpperCase();
   }

}
