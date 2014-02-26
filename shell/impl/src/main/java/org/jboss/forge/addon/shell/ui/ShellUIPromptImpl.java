/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.Buffer;
import org.jboss.aesh.console.command.CommandOperation;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.terminal.Key;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * Implementation of {@link UIPrompt}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIPromptImpl implements UIPrompt
{
   private final UIContext context;
   private final AeshConsole console;
   private final CommandInvocation commandInvocation;

   public ShellUIPromptImpl(UIContext context, AeshConsole console)
   {
      this.context = context;
      this.console = console;
      this.commandInvocation = (CommandInvocation) context.getAttributeMap()
               .get(CommandInvocation.class);
   }

   @Override
   public String prompt(String message)
   {
      if (isAcceptDefaultsEnabled())
      {
         return null;
      }
      PrintStream out = console.getShell().out();
      out.print(message);
      String output = readInput(out, true);
      out.println();
      return output;
   }

   @Override
   public String promptSecret(String message)
   {
      if (isAcceptDefaultsEnabled())
      {
         return null;
      }
      PrintStream out = console.getShell().out();
      out.print(message);
      String output = readInput(out, false);
      out.println();
      return output;
   }

   private String readInput(PrintStream out, boolean echo)
   {
      String output;
      try
      {
         StringBuilder sb = new StringBuilder();
         Key inputKey;
         do
         {
            CommandOperation input = commandInvocation.getInput();
            inputKey = input.getInputKey();
            if (inputKey == Key.BACKSPACE && sb.length() > 0)
            {
               sb.setLength(sb.length() - 1);
               if (echo)
               {
                  // move cursor left
                  out.print(Buffer.printAnsi("1D"));
                  out.flush();
                  // overwrite it with space
                  out.print(" ");
                  // move cursor back again
                  out.print(Buffer.printAnsi("1D"));
                  out.flush();
               }
            }
            else if (inputKey.isPrintable())
            {
               if (echo)
                  out.print(inputKey.getAsChar());

               sb.append(inputKey.getAsChar());
            }
         }
         while (inputKey != Key.ENTER && inputKey != Key.ENTER_2);
         output = (sb.length() == 0) ? null : sb.toString();
      }
      catch (InterruptedException e)
      {
         output = null;
      }
      return output;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      if (isAcceptDefaultsEnabled())
      {
         return true;
      }
      return "Y".equalsIgnoreCase(prompt(message + " [y/N]"));
   }

   private boolean isAcceptDefaultsEnabled()
   {
      Object acceptDefaultsFlag = context.getAttributeMap().get("ACCEPT_DEFAULTS");
      return acceptDefaultsFlag != null && "true".equalsIgnoreCase(acceptDefaultsFlag.toString());
   }

}
