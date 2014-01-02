/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalSize;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.util.CommandControllerComparator;
import org.jboss.forge.addon.ui.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CommandListCommand extends AbstractShellCommand
{
   private final CommandManager manager;
   private final CommandControllerFactory factory;

   @Inject
   public CommandListCommand(CommandManager manager, CommandControllerFactory factory)
   {
      this.manager = manager;
      this.factory = factory;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("command-list").description("List all available commands.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Shell shell = (Shell) context.getUIContext().getProvider();
      TerminalSize terminalSize = shell.getConsole().getShell().getSize();
      List<String> display = new ArrayList<>();

      Set<CommandController> controllers = new TreeSet<>(new CommandControllerComparator());
      for (UICommand command : manager.getAllCommands())
      {
         controllers.add(getCommandController(context, command));
      }

      for (CommandController controller : controllers)
      {
         String name = manager.getCommandName(context.getUIContext(), controller.getCommand());
         UICommandMetadata metadata = controller.getMetadata();
         display.add(metadata.getCategory()
                  + " > "
                  + new TerminalString(name, new TerminalColor(controller.isEnabled() ? Color.BLUE : Color.RED,
                           Color.DEFAULT)).toString() + " - " + metadata.getDescription());
      }

      UIOutput output = context.getUIContext().getProvider().getOutput();
      PrintStream out = output.out();
      out.println(Parser.formatDisplayList(display.toArray(new String[display.size()]),
               terminalSize.getHeight(), terminalSize.getWidth()));

      return Results.success();
   }

   private CommandController getCommandController(UIExecutionContext context, UICommand command)
   {
      return factory.createController(context.getUIContext(), command, new UIRuntime()
      {
         @Override
         public UIProgressMonitor createProgressMonitor(UIContext context)
         {
            return new DefaultUIProgressMonitor();
         }
      });
   }
}
