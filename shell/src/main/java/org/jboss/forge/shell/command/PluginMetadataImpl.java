/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.constraint.ConstraintEnforcer;
import org.jboss.forge.shell.constraint.ConstraintException;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.ConstraintInspector;

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

   private Set<Class<? extends Resource<?>>> resourceScopes = Collections.emptySet();

   private final Map<String, List<CommandMetadata>> commandMap = new HashMap<String, List<CommandMetadata>>();
   private final Map<String, Map<Class<? extends Resource<?>>, CommandMetadata>> commandAccessCache = new HashMap<String, Map<Class<? extends Resource<?>>, CommandMetadata>>();

   private CommandMetadata defaultCommand;
   private CommandMetadata setupCommand;

   private boolean scopeOverloaded = false;

   @Override
   public CommandMetadata getCommand(final String name)
   {
      return getCommand(name, (Class<? extends Resource<?>>) null);
   }

   @Override
   public CommandMetadata getCommand(final String name, final Shell shell)
   {
      return getCommand(name, shell.getCurrentResourceScope());
   }

   /**
    * Get the command matching the given name, or return null.
    */
   @Override
   public CommandMetadata getCommand(final String name, final Class<? extends Resource<?>> scope)
   {
      if (scope == null)
      {
         if (commandMap.containsKey(name) && (commandMap.get(name).size() > 1))
         {
            throw new RuntimeException("ambiguous query: overloaded commands exist. you must specify a scope.");
         }
         else
         {
            return commandMap.get(name).iterator().next();
         }
      }

      if (commandAccessCache.containsKey(name) && commandAccessCache.get(name).containsKey(scope))
      {
         return commandAccessCache.get(name).get(scope);
      }

      List<CommandMetadata> cmdMetadata = commandMap.get(name);
      if (cmdMetadata == null)
      {
         return null;
      }

      for (CommandMetadata c : cmdMetadata)
      {
         if (c.usableWithResource(scope))
         {
            return c;
         }
      }

      return null;
   }

   public Map<String, List<CommandMetadata>> getCommandMap()
   {
      return commandMap;
   }

   @Override
   public boolean hasCommand(final String name, final Shell shell)
   {
      return getCommand(name, shell.getCurrentResourceScope()) != null;
   }

   @Override
   public boolean hasCommand(final String name, final Class<? extends Resource<?>> scope)
   {
      return getCommand(name, scope) != null;
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
      return getCommands((Class<? extends Resource<?>>) null);
   }

   @Override
   public List<CommandMetadata> getCommands(final Shell shell)
   {
      return getCommands(shell.getCurrentResourceScope());
   }

   private List<CommandMetadata> getCommands(final Class<? extends Resource<?>> scope)
   {
      if ((scope == null) && scopeOverloaded)
      {
         throw new RuntimeException("ambiguous query: overloaded commands exist. you must specify a scope.");
      }

      List<CommandMetadata> result = new ArrayList<CommandMetadata>();
      for (List<CommandMetadata> cl : commandMap.values())
      {
         for (CommandMetadata c : cl)
         {
            if ((scope == null) || c.usableWithResource(scope))
            {
               result.add(c);
            }
         }
      }
      return Collections.unmodifiableList(result);
   }

   @Override
   public List<CommandMetadata> getAllCommands()
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
   public boolean isCommandOverloaded(final String name)
   {
      return commandMap.containsKey(name) && (commandMap.get(name).size() > 1);
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

   @Override
   public boolean constrantsSatisfied(final Shell shell)
   {
      try
      {
         ConstraintEnforcer enforcer = new ConstraintEnforcer();
         enforcer.verifyAvailable(shell.getCurrentProject(), this);
         return this.usableWithScope(shell.getCurrentResourceScope());
      }
      catch (ConstraintException e)
      {
         return false;
      }
   }

   @Override
   @SuppressWarnings("rawtypes")
   public boolean usableWithScope(final Class<? extends Resource> scope)
   {
      return resourceScopes.isEmpty() || resourceScopes.contains(scope);
   }

   @Override
   public Set<Class<? extends Resource<?>>> getResourceScopes()
   {
      return resourceScopes;
   }

   public void setResourceScopes(final List<Class<? extends Resource<?>>> resourceScopes)
   {
      this.resourceScopes = new HashSet<Class<? extends Resource<?>>>(resourceScopes);
   }

   @Override
   public boolean isSetupAvailable(final Shell shell)
   {
      if (hasSetupCommand())
      {
         if (!ConstraintInspector.requiresProject(getType())
                  || (ConstraintInspector.requiresProject(getType()) && (shell.getCurrentProject() != null)))
         {
            CommandMetadata setupCommand = getSetupCommand();
            return setupCommand.usableWithResource(shell.getCurrentResourceScope());
         }
      }
      return false;
   }
}
