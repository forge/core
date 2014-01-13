package org.jboss.forge.addon.shell;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.aesh.ShellSingleCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Manages {@link ShellSingleCommand} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class CommandManager
{
   private final AddonRegistry addonRegistry;

   private ConverterFactory converterFactory;
   private CommandFactory commandFactory;

   @Inject
   public CommandManager(final AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
   }

   public UICommand lookup(Class<? extends UICommand> type)
   {
      return addonRegistry.getServices(type).get();
   }

   public Set<String> getAllCommandNames(ShellContext shellContext)
   {
      Set<String> commands = new TreeSet<>();
      for (UICommand cmd : Commands.getEnabledCommands(getAllCommands(), shellContext))
      {
         commands.add(getCommandName(shellContext, cmd));
      }
      return commands;
   }

   public String getCommandName(UIContext context, UICommand cmd)
   {
      return ShellUtil.shellifyName(cmd.getMetadata(context).getName());
   }

   public Iterable<UICommand> getAllCommands()
   {
      if (commandFactory == null)
      {
         commandFactory = addonRegistry.getServices(CommandFactory.class).get();
      }
      return commandFactory.getCommands();
   }

   ConverterFactory getConverterFactory()
   {
      if (converterFactory == null)
      {
         converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      }
      return converterFactory;
   }
}
