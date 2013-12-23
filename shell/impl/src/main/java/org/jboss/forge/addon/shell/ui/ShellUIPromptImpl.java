/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;
import java.util.List;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.command.CommandOperation;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * Implementation of {@link UIPrompt}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIPromptImpl implements UIPrompt
{
   private final AeshConsole console;
   private final CommandInvocation commandInvocation;

   public ShellUIPromptImpl(AeshConsole console, CommandInvocation commandInvocation)
   {
      this.console = console;
      this.commandInvocation = commandInvocation;
   }

   private String toString(List<CommandOperation> operationList)
   {
      StringBuilder sb = new StringBuilder();
      for (CommandOperation commandOperation : operationList)
      {
         sb.append(commandOperation.getInputKey().getAsChar());
      }
      return sb.toString();
   }

   @Override
   public String prompt()
   {
      List<CommandOperation> input = commandInvocation.getInput();
      String output = toString(input);
      return output;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      PrintStream out = console.getShell().out();
      out.print(message + " [y/N]");
      List<CommandOperation> input = commandInvocation.getInput();
      out.println();
      String output = toString(input);
      return "Y".equalsIgnoreCase(output);
   }

}
