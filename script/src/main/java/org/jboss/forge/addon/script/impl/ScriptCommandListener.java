/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.script.impl;

import org.jboss.forge.addon.shell.CommandNotFoundListener;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

class ScriptCommandListener extends AbstractCommandExecutionListener implements CommandNotFoundListener
{
   Result result;

   @Override
   public void preCommandExecuted(UICommand command, UIExecutionContext context)
   {
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      synchronized (this)
      {
         this.result = result;
      }
   }

   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      synchronized (this)
      {
         this.result = Results.fail("Error encountered during command execution.", failure);
      }
   }

   @Override
   public void onCommandNotFound(String line, UIContext context)
   {
      synchronized (this)
      {
         this.result = Results.fail("Command not found: " + line);
      }
   }

   public boolean isExecuted()
   {
      synchronized (this)
      {
         return result != null;
      }
   }

   public Result getResult()
   {
      synchronized (this)
      {
         return result;
      }
   }
}