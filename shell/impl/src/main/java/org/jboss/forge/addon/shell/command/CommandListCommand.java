/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalSize;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Lists all the available commands
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandListCommand extends AbstractShellCommand
{
   @Inject
   private CommandFactory commandFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("command-list").description("List all available commands.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // No inputs needed
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Shell shell = (Shell) uiContext.getProvider();
      TerminalSize terminalSize = shell.getConsole().getShell().getSize();
      List<String> display = new ArrayList<>();

      Iterable<UICommand> commands = commandFactory.getCommands();
      for (UICommand command : commands)
      {
         UICommandMetadata metadata = command.getMetadata(uiContext);
         String name = commandFactory.getCommandName(uiContext, command);
         boolean enabled = command.isEnabled(uiContext);
         display.add(metadata.getCategory()
                  + " > "
                  + new TerminalString(name, new TerminalColor(enabled ? Color.CYAN : Color.RED,
                           Color.DEFAULT)).toString() + " - " + metadata.getDescription());
      }
      UIOutput output = uiContext.getProvider().getOutput();
      PrintStream out = output.out();
      out.println(Parser.formatDisplayList(display.toArray(new String[display.size()]),
               terminalSize.getHeight(), terminalSize.getWidth()));

      return Results.success();
   }
}
