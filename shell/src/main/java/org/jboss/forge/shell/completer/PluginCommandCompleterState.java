/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.parser.CommandParserContext;

/**
 * Holds state during TAB completion.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PluginCommandCompleterState extends BaseCommandCompleterState
{

   private PluginMetadata plugin;
   private CommandMetadata command;
   private OptionMetadata option;
   private CommandParserContext commandContext;

   public PluginCommandCompleterState(final String initialBuffer, final String lastBuffer, final int initialIndex)
   {
      super(initialBuffer, lastBuffer, initialIndex);
   }

   /*
    * Mutable state
    */
   public PluginMetadata getPlugin()
   {
      return plugin;
   }

   public void setPlugin(final PluginMetadata plugin)
   {
      this.plugin = plugin;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

   public void setCommand(final CommandMetadata command)
   {
      this.command = command;
   }

   public OptionMetadata getOption()
   {
      return option;
   }

   public void setOption(final OptionMetadata option)
   {
      this.option = option;
   }

   public CommandParserContext getCommandContext()
   {
      return commandContext;
   }

   public void setCommandContext(final CommandParserContext commandContext)
   {
      this.commandContext = commandContext;
   }
}
