/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * A listener for the {@link UICommand} execution lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandExecutionListener
{
   /**
    * Called when the given {@link UICommand} is about to be executed. Provides the current {@link UIExecutionContext}.
    */
   public void preCommandExecuted(UICommand command, UIExecutionContext context);

   /**
    * Called after the given {@link UICommand} has been executed. Provides the command {@link Result} and current
    * {@link UIExecutionContext}.
    */
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result);

   /**
    * Called after the given {@link UICommand} has been executed. Provides the command {@link Result} and current
    * {@link UIExecutionContext}.
    */
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure);

}
