package org.jboss.forge.addon.shell.aesh;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.furnace.services.Imported;

public class UICommandHelper
{
   @Inject
   private Imported<UICommand> allCommands;

   @Inject
   private CommandLineUtil commandLineUtil;

   public Iterable<UICommand> getAllCommands()
   {
      return allCommands;
   }

   public Iterable<UICommand> getMainCommands(ShellContext shellContext)
   {
      return Commands.getMainCommands(allCommands, shellContext);
   }

   public Iterable<ShellCommand> getMainShellCommands(Shell shell, UISelection<?> selection)
   {
      ShellContextImpl context = new ShellContextImpl(shell, selection);
      List<ShellCommand> commands = new ArrayList<ShellCommand>();
      for (UICommand cmd : getMainCommands(context))
      {
         ShellCommand shellCommand = new ShellCommand(cmd, context, commandLineUtil);
         commands.add(shellCommand);
      }
      return commands;
   }
   
   
}
