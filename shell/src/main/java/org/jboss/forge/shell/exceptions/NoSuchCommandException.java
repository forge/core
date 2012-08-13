/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.exceptions;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * User: christopherbrock Date: 1-Sep-2010 Time: 7:07:39 PM
 */
public class NoSuchCommandException extends CommandExecutionException
{
   private static final long serialVersionUID = -5108590337529122915L;

   public NoSuchCommandException(CommandMetadata command, String message)
   {
      super(command, message);
   }

   public NoSuchCommandException(CommandMetadata command, Throwable e)
   {
      super(command, e);
   }

   public NoSuchCommandException(CommandMetadata command, String message, Throwable e)
   {
      super(command, message, e);
   }
}
