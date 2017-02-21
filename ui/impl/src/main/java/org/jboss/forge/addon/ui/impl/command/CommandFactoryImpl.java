/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.command;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.impl.context.DelegatingUIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Sets;
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

   private Set<UICommand> cache = Sets.getConcurrentSet();

   private long version = -1;

   private static final Logger log = Logger.getLogger(CommandFactoryImpl.class.getName());

   @Override
   public Iterable<UICommand> getCommands()
   {
      return getCachedCommands();
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
            name = Commands.shellifyCommandName(name);
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
      return findCommand(getCommands(), context, name);
   }

   @Override
   public UICommand getNewCommandByName(UIContext context, String name)
   {
      return findCommand(getCommandsFromSource(), context, name);
   }

   private UICommand findCommand(Iterable<UICommand> commands, UIContext context, String name)
   {
      CommandNameUIProvider provider = new CommandNameUIProvider(context.getProvider());
      final UIContext delegatingContext = new DelegatingUIContext(context, provider);
      if (commands != null)
         for (UICommand cmd : commands)
         {
            // Test non-gui command name
            {
               provider.setGUI(false);
               String commandName = getCommandName(delegatingContext, cmd);
               if (Strings.compare(name, commandName)
                        || Strings.compare(name, Commands.shellifyCommandName(commandName)))
               {
                  return cmd;
               }
            }
            // Test gui command name
            {
               provider.setGUI(true);
               String commandName = getCommandName(delegatingContext, cmd);
               if (Strings.compare(name, commandName)
                        || Strings.compare(name, Commands.shellifyCommandName(commandName)))
               {
                  return cmd;
               }
            }
         }
      return null;

   }

   private Iterable<UICommand> getCachedCommands()
   {
      if (registry.getVersion() != version)
      {
         version = registry.getVersion();
         cache.clear();
         getCommands(cache::add);
      }
      return cache;
   }

   private Iterable<UICommand> getCommandsFromSource()
   {
      final Set<UICommand> result = Sets.getConcurrentSet();
      getCommands(result::add);
      return result;
   }

   private void getCommands(Consumer<UICommand> operation)
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
                  operation.accept(command);
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

   /**
    * {@link UIProvider} implementation for querying the command name in GUI and non-GUI modes, which is a common use
    * case for defining different names between GUI and CLI environments.
    * 
    * @author <a href="ggastald@redhat.com">George Gastaldi</a>
    */
   private static class CommandNameUIProvider implements UIProvider
   {
      private final UIProvider delegate;
      private boolean gui;

      public CommandNameUIProvider(UIProvider delegate)
      {
         this.delegate = delegate;
      }

      @Override
      public String getName()
      {
         return delegate.getName();
      }

      @Override
      public boolean isGUI()
      {
         return gui;
      }

      public void setGUI(boolean gui)
      {
         this.gui = gui;
      }

      @Override
      public UIOutput getOutput()
      {
         return delegate.getOutput();
      }

      @Override
      public UIDesktop getDesktop()
      {
         return delegate.getDesktop();
      }

      @Override
      public boolean isEmbedded()
      {
         return delegate.isEmbedded();
      }
   }

}
