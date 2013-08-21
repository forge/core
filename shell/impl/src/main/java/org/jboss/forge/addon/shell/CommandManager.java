package org.jboss.forge.addon.shell;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.aesh.CommandLineUtil;
import org.jboss.forge.addon.shell.aesh.ShellSingleCommand;
import org.jboss.forge.addon.shell.aesh.ShellWizard;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

/**
 * Manages {@link ShellSingleCommand} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandManager
{
   private final AddonRegistry addonRegistry;

   private Imported<UICommand> allCommands;
   private CommandLineUtil commandLineUtil;
   private ConverterFactory converterFactory;

   public CommandManager(AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
   }

   public UIWizardStep lookup(Class<? extends UIWizardStep> type)
   {
      return addonRegistry.getServices(type).get();
   }

   public Map<String, AbstractShellInteraction> getEnabledShellCommands(ShellContext shellContext)
   {
      Map<String, AbstractShellInteraction> commands = new HashMap<String, AbstractShellInteraction>();
      CommandLineUtil cmdLineUtil = getCommandLineUtil();
      for (UICommand cmd : Commands.getEnabledCommands(getAllCommands(), shellContext))
      {
         AbstractShellInteraction shellCommand;
         if (cmd instanceof UIWizard)
         {
            shellCommand = new ShellWizard((UIWizard) cmd, shellContext, cmdLineUtil, this);
         }
         else
         {
            shellCommand = new ShellSingleCommand(cmd, shellContext, cmdLineUtil);
         }
         commands.put(shellCommand.getName(), shellCommand);
      }
      return commands;
   }

   public Iterable<UICommand> getAllCommands()
   {
      if (allCommands == null)
      {
         allCommands = addonRegistry.getServices(UICommand.class);
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
