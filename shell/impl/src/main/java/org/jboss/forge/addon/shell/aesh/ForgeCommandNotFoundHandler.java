/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.List;

import org.jboss.aesh.console.settings.CommandNotFoundHandler;
import org.jboss.aesh.terminal.Shell;
import org.jboss.forge.addon.shell.CommandNotFoundListener;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandNotFoundHandler implements CommandNotFoundHandler
{
   private final List<CommandNotFoundListener> listeners;
   private final ShellImpl shellImpl;

   public ForgeCommandNotFoundHandler(ShellImpl shell, List<CommandNotFoundListener> listeners)
   {
      super();
      this.shellImpl = shell;
      this.listeners = listeners;
   }

   @Override
   public void handleCommandNotFound(String line, Shell shell)
   {
      try (ShellContextImpl uiContext = shellImpl.createUIContext())
      {
         for (CommandNotFoundListener listener : listeners)
         {
            listener.onCommandNotFound(line, uiContext);
         }
      }
   }
}
