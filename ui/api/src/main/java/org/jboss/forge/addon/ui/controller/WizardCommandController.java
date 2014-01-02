/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A composite implementation for {@link CommandController}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface WizardCommandController extends CommandController
{
   /**
    * Return the {@link UICommandMetadata} of the initial {@link UICommand} from which the underlying {@link UIWizard}
    * was launched.
    */
   UICommandMetadata getInitialMetadata();

   /**
    * Return <code>true</code> if navigation to the next {@link UIWizardStep} is possible.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   boolean canMoveToNextStep();

   /**
    * Return <code>true</code> if navigation to the previous {@link UIWizardStep} is possible.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   boolean canMoveToPreviousStep();

   /**
    * Navigate to the next {@link UIWizardStep}.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method or
    *            navigation is not possible.
    */
   WizardCommandController next() throws Exception;

   /**
    * Navigate to the previous {@link UIWizardStep}.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method or
    *            navigation is not possible
    */
   WizardCommandController previous() throws Exception;
}
