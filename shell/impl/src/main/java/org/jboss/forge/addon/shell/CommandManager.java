package org.jboss.forge.addon.shell;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.aesh.CommandLineUtil;
import org.jboss.forge.addon.shell.aesh.ShellSingleCommand;
import org.jboss.forge.addon.shell.aesh.ShellWizard;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizard;
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

   @Inject
   public CommandManager(final AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
   }

   public UICommand lookup(Class<? extends UICommand> type)
   {
      return addonRegistry.getServices(type).get();
   }

   public Map<String, AbstractShellInteraction> getEnabledShellCommands(ShellContext shellContext)
   {
      Map<String, AbstractShellInteraction> commands = new TreeMap<String, AbstractShellInteraction>();
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

   /**
    * Used in {@link ForgeCompletion} and {@link ForgeConsoleCallback}
    */
   public AbstractShellInteraction findCommand(ShellContext shellContext, String line)
   {
      String[] tokens = line.split(" ");
      if (tokens.length >= 1)
      {
         return getEnabledShellCommands(shellContext).get(tokens[0]);
      }
      return null;
   }

   public Collection<AbstractShellInteraction> findMatchingCommands(ShellContext shellContext, String line)
   {
      Set<AbstractShellInteraction> result = new TreeSet<AbstractShellInteraction>();

      String[] tokens = line == null ? new String[0] : line.split(" ");
      if (tokens.length <= 1)
      {
         Map<String, AbstractShellInteraction> commandMap = getEnabledShellCommands(shellContext);
         String token = (tokens.length == 1) ? tokens[0] : null;
         for (Entry<String, AbstractShellInteraction> entry : commandMap.entrySet())
         {
            if (token == null || entry.getKey().startsWith(token))
               result.add(entry.getValue());
         }
      }
      return result;
   }

}
