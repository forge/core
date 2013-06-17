/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import java.util.Map;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * Fired after a plugin/command has been executed and has finished processing.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * 
 */
public final class CommandExecuted
{
   public enum Status
   {
      SUCCESS, FAILURE
   }

   private Status status = Status.SUCCESS;
   private CommandMetadata command;
   private Object[] parameters;
   private String originalStatement;
   private Map<Object, Object> context;

   public CommandExecuted(final Status status, final CommandMetadata command, final String originalStatement,
            Object[] parameters, Map<Object, Object> context)
   {
      this.status = status;
      this.command = command;
      this.originalStatement = originalStatement;
      this.parameters = parameters;
      this.context = context;
   }

   public Status getStatus()
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

   public String getOriginalStatement()
   {
      return originalStatement;
   }

   public Map<Object, Object> getContext()
   {
      return context;
   }
}
