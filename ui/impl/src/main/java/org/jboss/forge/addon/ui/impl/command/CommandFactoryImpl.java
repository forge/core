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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

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
               UICommand command = iterator.next();
               if (!(command instanceof UIWizardStep))
               {
                  result.add(command);
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
      for (UICommand cmd : Commands.getEnabledCommands(getCommands(), context))
      {
         commands.add(getCommandName(context, cmd));
      }
      return commands;
   }

   @Override
   public String getCommandName(UIContext context, UICommand cmd)
   {
      String name = cmd.getMetadata(context).getName();
      if (!context.getProvider().isGUI())
      {
         name = shellifyName(name);
      }
      return name;
   }

   @Override
   public Set<String> getCommandNames(UIContext context)
   {
      Set<String> commands = new TreeSet<>();
      for (UICommand cmd : getCommands())
      {
         commands.add(getCommandName(context, cmd));
      }
      return commands;
   }

   @Override
   public UICommand getCommandByName(UIContext context, String name)
   {
      for (UICommand cmd : getCommands())
      {
         if (name.equals(getCommandName(context, cmd)))
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
