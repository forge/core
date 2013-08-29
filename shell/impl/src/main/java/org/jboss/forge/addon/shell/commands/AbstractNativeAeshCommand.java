/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.commands;

import java.io.IOException;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCommand;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractNativeAeshCommand extends AbstractShellCommand
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(ShellContext shellContext) throws Exception
   {
      Console console = shellContext.getProvider().getConsole();
      ConsoleCommand consoleCommand = getConsoleCommand(shellContext);
      console.attachProcess(consoleCommand);
      return Results.success();
   }

   public abstract ConsoleCommand getConsoleCommand(ShellContext context) throws IOException;
}
