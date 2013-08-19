/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.aesh.ShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandCompletion implements Completion
{
   private ShellImpl shell;

   public ForgeCommandCompletion(ShellImpl shell)
   {
      this.shell = shell;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      ShellContext shellContext = shell.newShellContext();
      Iterable<ShellCommand> commands = shell.findMatchingCommands(shellContext, completeOperation.getBuffer());
      for (ShellCommand cmd : commands)
      {
         String name = cmd.getName();
         completeOperation.addCompletionCandidate(name);
      }
   }

}
