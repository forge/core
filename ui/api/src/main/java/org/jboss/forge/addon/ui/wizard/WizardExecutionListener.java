/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.wizard;

import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * A {@link WizardExecutionListener} listens for wizard events
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface WizardExecutionListener extends CommandExecutionListener
{
   /**
    * Called when the given {@link UIWizard} is about to be executed. Provides the current {@link UIExecutionContext}.
    */
   void preWizardExecuted(UIWizard wizard, UIExecutionContext context);

   /**
    * Called after the given {@link UIWizard} has been executed. Provides the wizard {@link Result} and current
    * {@link UIExecutionContext}.
    */
   void postWizardExecuted(UIWizard wizard, UIExecutionContext context, Result result);

   /**
    * Called after the given {@link UIWizard} has been executed. Provides the command {@link Result} and current
    * {@link UIExecutionContext}.
    */
   void postWizardFailure(UIWizard wizard, UIExecutionContext context, Throwable failure);

}
