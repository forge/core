/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * An abstract class that implements the {@link CommandExecutionListener} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractCommandExecutionListener implements CommandExecutionListener
{

   @Override
   public void preCommandExecuted(UICommand command, UIExecutionContext context)
   {
      // do nothing
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      // do nothing
   }

   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      // do nothing

   }
}
