/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import java.util.Map;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * Fired when a command is vetoed
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class CommandVetoed
{
   private CommandMetadata command;
   private Object[] parameters;
   private String originalStatement;
   private Map<Object, Object> context;

   public CommandVetoed(CommandMetadata command, Object[] parameters, String originalStatement,
            Map<Object, Object> context)
   {
      super();
      this.command = command;
      this.parameters = parameters;
      this.originalStatement = originalStatement;
      this.context = context;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

   public void setCommand(CommandMetadata command)
   {
      this.command = command;
   }

   public Object[] getParameters()
   {
      return parameters;
   }

   public void setParameters(Object[] parameters)
   {
      this.parameters = parameters;
   }

   public String getOriginalStatement()
   {
      return originalStatement;
   }

   public void setOriginalStatement(String originalStatement)
   {
      this.originalStatement = originalStatement;
   }

   public Map<Object, Object> getContext()
   {
      return context;
   }
}
