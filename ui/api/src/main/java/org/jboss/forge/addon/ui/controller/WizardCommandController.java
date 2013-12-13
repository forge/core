/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A composite implementation for {@link CommandController}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface WizardCommandController extends CommandController
{
   /**
    * Return <code>true</code> if navigation to the next {@link UIWizardStep} is possible.
    */
   public boolean canMoveToNextStep();

   /**
    * Return <code>true</code> if navigation to the previous {@link UIWizardStep} is possible.
    */
   public boolean canMoveToPreviousStep();

   /**
    * Navigate to the next {@link UIWizardStep}.
    * 
    * @throws IllegalStateException if navigation is not possible
    */
   public WizardCommandController next() throws Exception;

   /**
    * Navigate to the previous {@link UIWizardStep}.
    * 
    * @throws IllegalStateException if navigation is not possible
    */
   public WizardCommandController previous() throws Exception;

   /**
    * Returns <code>true</code> if {@link UICommand#execute(org.jboss.forge.addon.ui.context.UIExecutionContext)} can be
    * called.
    */
   boolean canExecute();

}
