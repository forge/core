/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

/**
 * Event fired when a command is missing
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public final class CommandMissing
{
   private final String originalStatement;
   private final Object[] parameters;

   public CommandMissing(String originalStatement, Object[] parameters)
   {
      super();
      this.originalStatement = originalStatement;
      this.parameters = parameters;
   }

   public String getOriginalStatement()
   {
      return originalStatement;
   }

   public Object[] getParameters()
   {
      return parameters;
   }
}