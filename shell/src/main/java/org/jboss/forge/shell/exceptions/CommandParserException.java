/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.exceptions;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CommandParserException extends ShellParserException
{
   private static final long serialVersionUID = -6474891123733228235L;
   private final CommandMetadata command;

   public CommandParserException(final CommandMetadata command, final String message)
   {
      super(message);
      this.command = command;
   }

   public CommandParserException(final CommandMetadata command, final Throwable e)
   {
      super(e);
      this.command = command;
   }

   public CommandParserException(final CommandMetadata command, final String message, final Throwable e)
   {
      super(message, e);
      this.command = command;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

}
