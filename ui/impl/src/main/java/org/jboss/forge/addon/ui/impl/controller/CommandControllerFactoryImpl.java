/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.SingleCommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Creates {@link CommandController} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class CommandControllerFactoryImpl implements CommandControllerFactory
{
   private final AddonRegistry addonRegistry;

   @Inject
   public CommandControllerFactoryImpl(AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
   }

   @Override
   public CommandController createController(UIContext context, UICommand command, UIRuntime runtime)
   {
      if (command instanceof UIWizard)
         return createWizardController(context, (UIWizard) command, runtime);
      return createSingleController(context, command, runtime);
   }

   @Override
   public SingleCommandController createSingleController(UIContext context, UICommand command, UIRuntime runtime)
   {
      return new SingleCommandControllerImpl(addonRegistry, runtime, command, context);
   }

   @Override
   public WizardCommandController createWizardController(UIContext context, UIWizard wizard, UIRuntime runtime)
   {
      return new WizardCommandControllerImpl(context, addonRegistry, runtime, wizard, this);
   }
}
