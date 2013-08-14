/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;

import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Hook for Aesh operations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeConsoleCallback implements ConsoleCallback
{
   private final Shell shell;
   private final AddonRegistry addonRegistry;

   private final Iterable<UICommand> allCommands;
   private final CommandLineUtil commandLineUtil;

   public ForgeConsoleCallback(Shell shell, AddonRegistry addonRegistry)
   {
      this.shell = shell;
      this.addonRegistry = addonRegistry;

      this.allCommands = addonRegistry.getServices(UICommand.class);
      this.commandLineUtil = addonRegistry.getServices(CommandLineUtil.class).get();
   }

   /**
    * This method will be called when a user press the "enter/return" key. The return value is to indicate if the
    * outcome was a success or not. Return 0 for success and something else for failure (typical 1 or -1).
    */
   @Override
   public int readConsoleOutput(ConsoleOutput output) throws IOException
   {
//      Commands.getMainCommands(allCommands, null)
      for (UICommand command : allCommands)
      {
      }
      return 0;
   }
}
