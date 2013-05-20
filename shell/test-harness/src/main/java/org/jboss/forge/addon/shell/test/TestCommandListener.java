/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test;

import javax.inject.Singleton;

import org.jboss.forge.addon.shell.spi.CommandExecutionListener;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class TestCommandListener implements CommandExecutionListener
{
   Result result;

   @Override
   public void preCommandExecuted(UICommand command, UIContext context)
   {
   }

   @Override
   public void postCommandExecuted(UICommand command, UIContext context, Result result)
   {
      synchronized (this)
      {
         this.result = result;
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

   public void reset()
   {
      synchronized (this)
      {
         result = null;
      }
   }
}