/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

/**
 * A listener for the {@link UICommand} execution lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandExecutionListener
{
   /**
    * Called when the given {@link UICommand} is about to be executed. Provides the current {@link UIExecutionContext}.
    */
   void preCommandExecuted(UICommand command, UIExecutionContext context);

   /**
    * Called after the given {@link UICommand} has been executed. Provides the current {@link UIExecutionContext}
    * command and the {@link Result}. The {@link Result} can be a {@link Failed} result containing a {@link Throwable}
    * 
    * @see Failed#getException()
    */
   void postCommandExecuted(UICommand command, UIExecutionContext context, Result result);

   /**
    * Called after the given {@link UICommand} has been executed and an exception was thrown. Provides the
    * {@link UIExecutionContext} and the {@link Throwable} that caused the command to fail
    */
   void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure);
}