/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.command.UICommandTransformer;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.SingleCommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

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
   private final Imported<UICommandTransformer> transformers;
   private final Logger log = Logger.getLogger(getClass().getName());

   @Inject
   public CommandControllerFactoryImpl(AddonRegistry addonRegistry)
   {
      this.addonRegistry = addonRegistry;
      this.transformers = this.addonRegistry.getServices(UICommandTransformer.class);
   }

   @Override
   public CommandController createController(final UIContext context, final UIRuntime runtime,
            final UICommand originalCommand)
   {
      UICommand command = transform(context, originalCommand);
      if (command instanceof UIWizard)
      {
         return doCreateWizardController(context, runtime, (UIWizard) command);
      }
      else
      {
         return doCreateSingleController(context, runtime, command);
      }
   }

   @Override
   public SingleCommandController createSingleController(final UIContext context, final UIRuntime runtime,
            final UICommand originalCommand)
   {
      final UICommand command = transform(context, originalCommand);
      return doCreateSingleController(context, runtime, command);
   }

   @Override
   public WizardCommandController createWizardController(final UIContext context, final UIRuntime runtime,
            final UIWizard wizard)
   {
      final UICommand command = transform(context, wizard);
      if (command instanceof UIWizard)
      {
         return doCreateWizardController(context, runtime, (UIWizard) command);
      }
      else
      {
         throw new IllegalStateException("Impossible to create a WizardController from enriched command " + command);
      }
   }

   private UICommand transform(final UIContext context, final UICommand originalCommand)
   {
      UICommand command = originalCommand;
      for (UICommandTransformer transformer : transformers)
      {
         UICommand tmpCommand = transformer.transform(context, command);
         if (tmpCommand == null)
         {
            log.warning(UICommandTransformer.class.getName() + " implementation "
                     + transformer.getClass().getName()
                     + " should not have returned null. Ignoring.");
         }
         else
         {
            command = tmpCommand;
         }
      }
      return command;
   }

   protected WizardCommandController doCreateWizardController(final UIContext context, final UIRuntime runtime,
            final UIWizard wizard)
   {
      WizardCommandControllerImpl controller = new WizardCommandControllerImpl(context, addonRegistry, runtime,
               wizard, this);
      return new NoUIWizardControllerDecorator(controller);
   }

   protected SingleCommandController doCreateSingleController(final UIContext context, final UIRuntime runtime,
            final UICommand command)
   {
      return new SingleCommandControllerImpl(addonRegistry, runtime, command, context);
   }

}
