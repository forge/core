/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;
import java.util.List;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Strings;

/**
 * Hook for Aesh operations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeConsoleCallback implements ConsoleCallback
{
   private final ShellImpl shell;

   public ForgeConsoleCallback(ShellImpl shell)
   {
      this.shell = shell;
   }

   /**
    * This method will be called when a user press the "enter/return" key. The return value is to indicate if the
    * outcome was a success or not. Return 0 for success and something else for failure (typical 1 or -1).
    */
   @Override
   public int readConsoleOutput(ConsoleOutput output) throws IOException
   {
      String line = output.getBuffer();
      Console console = shell.getConsole();
      if (!Strings.isNullOrEmpty(line))
      {
         try
         {
            ShellContext context = shell.newShellContext();
            AbstractShellInteraction command = shell.findCommand(context, line);
            if (command == null)
            {
               throw new IOException("Command not found for line: " + line);
            }
            command.populateInputs(line, false);
            List<String> errors = command.validate();
            if (errors.isEmpty())
            {
               Result result = shell.execute(command);
               if (result != null && result.getMessage() != null)
               {
                  console.out().println(result.getMessage());
               }
            }
            else
            {
               // Display the error messages
               for (String error : errors)
               {
                  console.err().println("**ERROR**: " + error);
               }
            }
         }
         catch (Exception e)
         {
            console.err().println("**ERROR**: " + e.getMessage());
            // if VERBOSE = true
            // e.printStackTrace(console.err());
            return -1;
         }
      }
      return 0;
   }
}
