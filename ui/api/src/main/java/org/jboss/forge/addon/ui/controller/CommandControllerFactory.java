/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Creates {@link CommandController} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandControllerFactory
{
   /**
    * Create a {@link CommandController} of the correct type for the given {@link UICommand}.
    */
   CommandController createController(UIContext context, UIRuntime runtime, UICommand command);

   /**
    * Create a {@link SingleCommandController}. Should be called when a single {@link UICommand} execution must be
    * performed.
    */
   SingleCommandController createSingleController(UIContext context, UIRuntime runtime, UICommand command);

   /**
    * Create a {@link WizardCommandController}. Should be called when a {@link UIWizard} (multiple step) execution must
    * be performed.
    */
   WizardCommandController createWizardController(UIContext context, UIRuntime runtime, UIWizard wizard);
}
