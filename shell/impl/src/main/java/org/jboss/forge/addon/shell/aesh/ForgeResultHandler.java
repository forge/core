/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.cl.result.ResultHandler;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * Logs Aesh execution failures
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeResultHandler implements ResultHandler
{
   private static final Logger log = Logger.getLogger(ForgeResultHandler.class.getName());
   private final ShellContext context;
   private final String commandName;

   public ForgeResultHandler(ShellContext context, String commandName)
   {
      this.context = context;
      this.commandName = commandName;
   }

   @Override
   public void onSuccess()
   {
   }

   @Override
   public void onFailure(CommandResult result)
   {
   }

   @Override
   public void onValidationFailure(CommandResult result, Exception exception)
   {
      UIOutput output = context.getProvider().getOutput();
      PrintStream err = output.err();
      log.log(Level.SEVERE, "Error while validating command '" + commandName + "'", exception);
      if (exception != null)
      {
         if (context.isVerbose())
         {
            exception.printStackTrace(err);
         }
         else
         {
            output.info(err, "(type \"export VERBOSE=true\" to enable stack traces)");
         }

      }
   }
}
