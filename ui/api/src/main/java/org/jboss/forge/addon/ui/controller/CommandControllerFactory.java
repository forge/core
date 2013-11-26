/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Creates {@link CommandController} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandControllerFactory
{
   /**
    * Create a {@link SingleCommandController}. Should be called when a single command execution must be performed.
    */
   CommandController createSingleController(Class<? extends UICommand> command, UIContextFactory uiFactory);

   /**
    * Create a {@link WizardCommandController}. Should be called when a wizard (multisteps) execution must be performed.
    */
   WizardCommandController createWizardController(Class<? extends UIWizard> wizard, UIContextFactory uiFactory);
}
