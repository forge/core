package org.jboss.forge.addon.shell.aesh;

import javax.inject.Inject;

import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.furnace.services.Imported;

public class UICommandHelper
{
   @Inject
   private Imported<UICommand> allCommands;

   @Inject
   private CommandLineUtil commandLineUtil;

   private ShellContext shellContext;

   public UICommandHelper(ShellContext context)
   {
      this.shellContext = context;
   }

   public Iterable<UICommand> getAllCommands()
   {
      return allCommands;
   }

   public Iterable<UICommand> getMainCommands()
   {
      return Commands.getMainCommands(allCommands, null);
   }
}
