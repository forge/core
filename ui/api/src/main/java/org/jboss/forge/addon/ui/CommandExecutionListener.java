/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.services.Exported;

/**
 * A listener for the {@link UICommand} execution lifecycle.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface CommandExecutionListener
{
   /**
    * Called when the given {@link UICommand} is about to be executed. Provides the current {@link UIContext}.
    */
   public void preCommandExecuted(UICommand command, UIContext context);

   /**
    * Called after the given {@link UICommand} has been executed. Provides the command {@link Result} and current
    * {@link UIContext}.
    */
   public void postCommandExecuted(UICommand command, UIContext context, Result result);

   /**
    * Called after the given {@link UICommand} has been executed. Provides the command {@link Result} and current
    * {@link UIContext}.
    */
   public void postCommandFailure(UICommand command, UIContext context, Throwable failure);

}
