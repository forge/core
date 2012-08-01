/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.shell.events;

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
      SUCCESS, FAILURE, MISSING
   }

   private Status status = Status.SUCCESS;
   private CommandMetadata command;
   private Object[] parameters;
   private String originalStatement;

   public CommandExecuted()
   {
   }

   public CommandExecuted(final Status status, final CommandMetadata command, final String originalStatement,
            Object[] parameters)
   {
      this.status = status;
      this.command = command;
      this.originalStatement = originalStatement;
      this.parameters = parameters;
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
}
