/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.command;

import org.jboss.forge.plugin.meta.CommandMetadata;

/**
 * Fired after a plugin/command has been executed and has finished processing.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class CommandExecuted
{
   public enum CommandStatus
   {
      SUCCESS, FAILURE
   }

   private CommandStatus status = CommandStatus.SUCCESS;
   private CommandMetadata command;
   private Object[] parameters;

   public CommandExecuted()
   {
   }

   public CommandExecuted(final CommandStatus status, final CommandMetadata command, final String originalStatement,
            Object[] parameters)
   {
      this.status = status;
      this.command = command;
      this.parameters = parameters;
   }

   public CommandStatus getStatus()
   {
      return status;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

   public Object[] getParameters()
   {
      return parameters;
   }

}
