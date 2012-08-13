/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class CompletedCommandHolder
{
   private PluginCommandCompleterState state;

   public void setState(PluginCommandCompleterState state)
   {
      this.state = state;
   }

   public PluginCommandCompleterState getState()
   {
      return state;
   }
}
