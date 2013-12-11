/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UICommand;

/**
 * A composite implementation for {@link CommandController}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface WizardCommandController extends CommandController
{
   /**
    * Is it possible to navigate to the next page?
    */
   public boolean canMoveToNextStep();

   /**
    * Is it possible to navigate to the previous page?
    */
   public boolean canMoveToPreviousStep();

   /**
    * Navigate to the next page.
    * 
    * @throws IllegalStateException if navigation is not possible
    */
   public WizardCommandController next() throws IllegalStateException;

   /**
    * Navigate to the previous visited page
    * 
    * @throws IllegalStateException if navigation is not possible
    */
   public WizardCommandController previous() throws IllegalStateException;

   /**
    * Returns <code>true</code> if {@link UICommand#execute(org.jboss.forge.addon.ui.context.UIExecutionContext)} can be
    * called
    */
   boolean canExecute();

}
