/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Displays the command execution failure if VERBOSE is set to true
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class VerboseExecutionListener extends AbstractCommandExecutionListener
{
   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      if (result instanceof Failed)
      {
         postCommandFailure(command, context, ((Failed) result).getException());
      }
   }

   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      if (failure != null)
      {
         UIContext uiContext = context.getUIContext();
         if (uiContext instanceof ShellContext)
         {
            ShellContext shellContext = (ShellContext) uiContext;
            UIOutput output = shellContext.getProvider().getOutput();
            PrintStream err = output.err();
            UICommandMetadata metadata = command.getMetadata(shellContext);
            if (metadata != null)
               output.error(err, "Error while executing '" + metadata.getName() + "'");
            if (shellContext.isVerbose())
            {
               failure.printStackTrace(err);
            }
            else
            {
               output.info(err, "(type \"export VERBOSE=true\" to enable stack traces)");
            }
         }
      }
   }
}
