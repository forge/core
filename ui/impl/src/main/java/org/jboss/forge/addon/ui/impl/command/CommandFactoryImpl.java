/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Strings;

/**
 * Creates and manages commands
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class CommandFactoryImpl implements CommandFactory
{
   @Inject
   private AddonRegistry registry;

   private static final Logger log = Logger.getLogger(CommandFactoryImpl.class.getName());

   @Override
   public Iterable<UICommand> getCommands()
   {
      Set<UICommand> result = new HashSet<>();
      synchronized (this)
      {
         Imported<CommandProvider> instances = registry.getServices(CommandProvider.class);
         for (CommandProvider provider : instances)
         {
            Iterable<UICommand> commands = provider.getCommands();
            Iterator<UICommand> iterator = commands.iterator();
            while (iterator.hasNext())
            {
               try
               {
                  UICommand command = iterator.next();
                  if (!(command instanceof UIWizardStep))
                  {
                     result.add(command);
                  }
               }
               catch (Exception e)
               {
                  log.log(Level.SEVERE, "Error while retrieving command instance", e);
               }
            }
            instances.release(provider);
         }
      }
      return result;
   }

   @Override
   public Set<String> getEnabledCommandNames(UIContext context)
   {
      Set<String> commands = new TreeSet<>();
      Iterable<UICommand> allCommands = getCommands();
      for (UICommand cmd : allCommands)
      {
         try
         {
            if (Commands.isEnabled(cmd, context))
            {
               commands.add(getCommandName(context, cmd));
            }
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Error while checking if command " + cmd + " isEnabled", e);
         }
      }
      return commands;
   }

   @Override
   public String getCommandName(UIContext context, UICommand cmd)
   {
      String name = null;
      try
      {
         UICommandMetadata metadata = cmd.getMetadata(context);
         name = metadata.getName();
         if (!context.getProvider().isGUI())
         {
            name = shellifyName(name);
         }
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Error while getting command name for " + cmd.getClass(), e);
      }
      return name;
   }

   @Override
   public Set<String> getCommandNames(UIContext context)
   {
      Set<String> commands = new TreeSet<>();
      for (UICommand cmd : getCommands())
      {
         String commandName = getCommandName(context, cmd);
         if (commandName != null)
         {
            commands.add(commandName);
         }
      }
      return commands;
   }

   @Override
   public UICommand getCommandByName(UIContext context, String name)
   {
      for (UICommand cmd : getCommands())
      {
         String commandName = getCommandName(context, cmd);
         if (Strings.compare(name, commandName) || Strings.compare(name, shellifyName(commandName)))
         {
            return cmd;
         }
      }
      return null;
   }

   /**
    * "Shellifies" a name (that is, makes the name shell-friendly) by replacing spaces with "-" and removing colons
    */
   private static String shellifyName(String name)
   {
      return name.trim().toLowerCase().replaceAll("\\W+", "-").replaceAll("\\:", "");
   }

}
