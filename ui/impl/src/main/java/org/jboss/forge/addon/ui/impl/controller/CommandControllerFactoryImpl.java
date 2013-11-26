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
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Creates {@link CommandController} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
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
   public CommandController createSingleController(Class<? extends UICommand> command, UIContextFactory uiFactory)
   {
      return null;
   }

   @Override
   public WizardCommandController createWizardController(Class<? extends UIWizard> wizard, UIContextFactory uiFactory)
   {
      return null;
   }

   public CommandController create(Class<? extends UICommand> command, UIContext context)
   {
      if (UIWizardStep.class.isAssignableFrom(command))
      {
         throw new IllegalArgumentException("Class " + command.getName()
                  + " implements UIWizardStep, and it's not possible to start from a step");
      }
      else if (UIWizard.class.isAssignableFrom(command))
      {
         return null;
      }
      else if (UICommand.class.isAssignableFrom(command))
      {
         // return new SingleCommandController(addonRegistry, addonRegistry.getServices(command).get());
      }
      throw new IllegalArgumentException(command + " is not a valid UICommand");
   }
}
