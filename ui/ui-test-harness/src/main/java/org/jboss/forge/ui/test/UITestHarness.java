/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.UIProviderImpl;
import org.jboss.forge.ui.test.impl.UIRuntimeImpl;

/**
 * Creates {@link CommandController} objects for the purpose of unit testing
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UITestHarness
{
   @Inject
   private AddonRegistry addonRegistry;

   @Inject
   private CommandControllerFactory factory;

   public CommandController createCommandController(Class<? extends UICommand> commandClass) throws Exception
   {
      return createCommandController(commandClass, (Resource<?>) null);
   }

   public CommandController createCommandController(Class<? extends UICommand> commandClass,
            Resource<?>... initialSelection) throws Exception
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      UISelection<Resource<?>> selection = Selections.from(initialSelection);
      UIContextImpl context = new UIContextImpl(new UIProviderImpl(true), listeners, selection);
      return factory
               .createSingleController(context, new UIRuntimeImpl(), addonRegistry.getServices(commandClass).get());
   }

   public WizardCommandController createWizardController(Class<? extends UIWizard> wizardClass) throws Exception
   {
      return createWizardController(wizardClass, (Resource<?>) null);
   }

   public WizardCommandController createWizardController(Class<? extends UIWizard> wizardClass,
            Resource<?>... initialSelection) throws Exception
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      UISelection<Resource<?>> selection = Selections.from(initialSelection);
      UIContextImpl context = new UIContextImpl(new UIProviderImpl(true), listeners, selection);
      return factory.createWizardController(context, new UIRuntimeImpl(),
               addonRegistry.getServices(wizardClass).get());
   }
}
