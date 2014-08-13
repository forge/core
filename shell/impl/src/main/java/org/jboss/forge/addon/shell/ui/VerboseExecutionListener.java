/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.forge.addon.shell.ShellMessages;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;

/**
 * Displays the command execution failure if VERBOSE is set to true
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class VerboseExecutionListener extends AbstractCommandExecutionListener
{
   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      UIContext uiContext = context.getUIContext();
      if (uiContext instanceof ShellContext)
      {
         ShellContext shellContext = (ShellContext) uiContext;
         if (shellContext.isVerbose() && failure != null)
         {
            PrintStream err = shellContext.getProvider().getOutput().err();
            UICommandMetadata metadata = command.getMetadata(shellContext);
            if (metadata != null)
               ShellMessages.error(err, "Error while executing '" + metadata.getName() + "'");
            failure.printStackTrace(err);
         }
      }
   }

}
