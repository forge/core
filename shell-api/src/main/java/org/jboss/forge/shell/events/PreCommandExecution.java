/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * Fired before a plugin/command is executed
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public final class PreCommandExecution
{
   private CommandMetadata command;
   private Object[] parameters;
   private String originalStatement;
   private boolean vetoed;

   public PreCommandExecution()
   {
   }

   public PreCommandExecution(final CommandMetadata command, final String originalStatement,
            Object[] parameters)
   {
      this.command = command;
      this.originalStatement = originalStatement;
      this.parameters = parameters;
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

   public boolean isVetoed()
   {
      return vetoed;
   }

   /**
    * Vetoes the execution of this command
    */
   public void veto()
   {
      this.vetoed = true;
   }
}
