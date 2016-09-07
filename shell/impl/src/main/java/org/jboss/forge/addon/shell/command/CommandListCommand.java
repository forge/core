/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

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
import org.jboss.forge.furnace.util.OperatingSystemUtils;

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
   private static final String CMD_LIST_HELP_DESCRIPTION = "List all available commands. Each line starts with the category to which a command belongs followed by the name and the command description."
           + OperatingSystemUtils.getLineSeparator()
           + "When a command is not available within the context of the shell where it is executed, then the name of the command is displayed in red otherwise in green."
           + OperatingSystemUtils.getLineSeparator()
           + "By example, it is not possible to add a module or fraction using the command 'wildfly-swarm-add-fraction' if the Wildfly Swarm project doesn't exist !";

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass())
                     .name("command-list")
                     .description("List all available commands.")
                     .longDescription(CMD_LIST_HELP_DESCRIPTION);
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
      Set<CommandInfo> commandInfos = new TreeSet<>();
      for (UICommand command : commandFactory.getCommands())
      {
         UICommandMetadata metadata = command.getMetadata(uiContext);
         String name = commandFactory.getCommandName(uiContext, command);
         boolean enabled = command.isEnabled(uiContext);
         commandInfos.add(new CommandInfo(metadata.getCategory().toString(), name, metadata.getDescription(), enabled));
      }
      for (CommandInfo command : commandInfos)
      {
         display.add(command.category
                  + " > "
                  + new TerminalString(command.name, new TerminalColor(command.enabled ? Color.CYAN : Color.RED,
                           Color.DEFAULT)).toString()
                  + " - " + command.description);
      }
      UIOutput output = uiContext.getProvider().getOutput();
      PrintStream out = output.out();
      out.println(Parser.formatDisplayList(display.toArray(new String[display.size()]),
               terminalSize.getHeight(), terminalSize.getWidth()));

      return Results.success();
   }

   private static class CommandInfo implements Comparable<CommandInfo>
   {
      final String category;
      final String name;
      final String description;
      final boolean enabled;

      CommandInfo(String category, String name, String description, boolean enabled)
      {
         super();
         this.category = category;
         this.name = name;
         this.description = description;
         this.enabled = enabled;
      }

      @Override
      public int compareTo(CommandInfo o)
      {
         int compareTo = this.category.compareTo(o.category);
         if (compareTo == 0)
         {
            compareTo = this.name.compareTo(o.name);
         }
         return compareTo;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((category == null) ? 0 : category.hashCode());
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         CommandInfo other = (CommandInfo) obj;
         if (category == null)
         {
            if (other.category != null)
               return false;
         }
         else if (!category.equals(other.category))
            return false;
         if (name == null)
         {
            if (other.name != null)
               return false;
         }
         else if (!name.equals(other.name))
            return false;
         return true;
      }

   }
}
