/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
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
      String output;
      try
      {
         output = String.valueOf(commandInvocation.getInput().getInputKey().getAsChar());
      }
      catch (InterruptedException e)
      {
         output = null;
      }
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
      String output;
      try
      {
         output = String.valueOf(commandInvocation.getInput().getInputKey().getAsChar());
      }
      catch (InterruptedException e)
      {
         output = null;
      }
      out.println();
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
