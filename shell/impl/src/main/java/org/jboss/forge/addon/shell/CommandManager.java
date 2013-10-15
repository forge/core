package org.jboss.forge.addon.shell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.aesh.CommandLineUtil;
import org.jboss.forge.addon.shell.aesh.ShellSingleCommand;
import org.jboss.forge.addon.shell.aesh.ShellWizard;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.services.Imported;

/**
 * Manages {@link ShellSingleCommand} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class CommandManager
{
   private final AddonRegistry addonRegistry;

   private Imported<UICommand> allCommands;
   private CommandLineUtil commandLineUtil;
   private ConverterFactory converterFactory;

   private List<UICommand> commandCache;

   @Inject
   public CommandManager(final AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
   }

   public void addonStarted(@Observes PostStartup event)
   {
      commandCache = null;
   }

   public void addonStopped(@Observes PreShutdown event)
   {
      commandCache = null;
   }

   public UICommand lookup(Class<? extends UICommand> type)
   {
      return addonRegistry.getServices(type).get();
   }

   public Set<String> getAllCommandNames(ShellContext shellContext)
   {
      Set<String> commands = new HashSet<String>();
      for (UICommand cmd : Commands.getEnabledCommands(getAllCommands(), shellContext))
      {
         commands.add(getCommandName(shellContext, cmd));
      }
      return commands;
   }

   public AbstractShellInteraction findCommand(ShellContext shellContext, String commandName)
   {
      AbstractShellInteraction result = null;
      CommandLineUtil cmdLineUtil = getCommandLineUtil();
      for (UICommand cmd : Commands.getEnabledCommands(getAllCommands(), shellContext))
      {
         if (commandName.equals(getCommandName(shellContext, cmd)))
         {
            if (cmd instanceof UIWizard)
            {
               result = new ShellWizard((UIWizard) cmd, shellContext, cmdLineUtil, this);
            }
            else
            {
               result = new ShellSingleCommand(cmd, shellContext, cmdLineUtil);
            }
            break;
         }
      }
      return result;
   }

   private String getCommandName(ShellContext shellContext, UICommand cmd)
   {
      return ShellUtil.shellifyName(cmd.getMetadata(shellContext).getName());
   }

   public Iterable<UICommand> getAllCommands()
   {
      if (allCommands == null)
      {
         allCommands = addonRegistry.getServices(UICommand.class);
      }
      if (commandCache == null)
      {
         commandCache = new ArrayList<UICommand>();
         for (UICommand command : allCommands)
         {
            commandCache.add(command);
         }
      }
      return allCommands;
   }

   private CommandLineUtil getCommandLineUtil()
   {
      if (commandLineUtil == null)
      {
         commandLineUtil = new CommandLineUtil(getConverterFactory());
      }
      return commandLineUtil;
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
