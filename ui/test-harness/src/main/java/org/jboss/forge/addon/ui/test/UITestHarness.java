/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.test;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.test.impl.UIContextImpl;
import org.jboss.forge.addon.ui.test.impl.UIProviderImpl;
import org.jboss.forge.addon.ui.test.impl.UIRuntimeImpl;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

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
   private CommandFactory commandFactory;

   @Inject
   private CommandControllerFactory factory;

   private UIProviderImpl providerImpl;
   
   private boolean isGui = true;

   private final Map<String, String> promptResults = new HashMap<>();

   public CommandController createCommandController(String name) throws Exception
   {
      return createCommandController(name, (Resource<?>) null);
   }

   public CommandController createCommandController(String name, Resource<?>... initialSelection) throws Exception
   {
      CommandController result = null;
      Iterable<UICommand> commands = commandFactory.getCommands();
      UIContextImpl context = getUIContextInstance(initialSelection);
      for (UICommand command : commands)
      {
         UICommandMetadata metadata = command.getMetadata(context);
         if (name.equals(metadata.getName()))
         {
            result = factory.createSingleController(context, getUIRuntimeInstance(), command);
            break;
         }
      }
      Assert.notNull(result, "Command " + name + " not found");
      return result;
   }

   public CommandController createCommandController(Class<? extends UICommand> commandClass) throws Exception
   {
      return createCommandController(commandClass, (Resource<?>) null);
   }

   public CommandController createCommandController(Class<? extends UICommand> commandClass,
            Resource<?>... initialSelection) throws Exception
   {
      return factory.createSingleController(
               getUIContextInstance(initialSelection),
               getUIRuntimeInstance(),
               addonRegistry.getServices(commandClass).get());
   }

   public WizardCommandController createWizardController(String name) throws Exception
   {
      return createWizardController(name, (Resource<?>) null);
   }

   public WizardCommandController createWizardController(String name, Resource<?>... initialSelection) throws Exception
   {
      WizardCommandController result = null;
      Iterable<UICommand> commands = commandFactory.getCommands();
      UIContextImpl context = getUIContextInstance(initialSelection);
      for (UICommand command : commands)
      {
         if (command instanceof UIWizard)
         {
            UICommandMetadata metadata = command.getMetadata(context);
            if (name.equals(metadata.getName()))
            {
               result = factory.createWizardController(context, getUIRuntimeInstance(), (UIWizard) command);
               break;
            }
         }
      }
      Assert.notNull(result, "Command " + name + " not found");
      return result;
   }

   public WizardCommandController createWizardController(Class<? extends UIWizard> wizardClass) throws Exception
   {
      return createWizardController(wizardClass, (Resource<?>) null);
   }

   public WizardCommandController createWizardController(Class<? extends UIWizard> wizardClass,
            Resource<?>... initialSelection) throws Exception
   {
      return factory.createWizardController(
               getUIContextInstance(initialSelection),
               getUIRuntimeInstance(),
               addonRegistry.getServices(wizardClass).get());
   }

   private UIRuntimeImpl getUIRuntimeInstance()
   {
      return new UIRuntimeImpl(promptResults);
   }

   public UIProviderImpl getProvider()
   {
      if (providerImpl == null)
      {
         providerImpl = new UIProviderImpl(isGui);
      }
      return providerImpl;
   }
   
   private UIContextImpl getUIContextInstance(Resource<?>... initialSelection)
   {
      Imported<UIContextListener> listeners = addonRegistry.getServices(UIContextListener.class);
      UISelection<Resource<?>> selection = Selections.from(initialSelection);
      UIContextImpl context = new UIContextImpl(getProvider(), listeners, selection);
      return context;
   }

   /**
    * The {@link Map} is based on the key being a regular expression to the prompt message
    */
   public Map<String, String> getPromptResults()
   {
      return this.promptResults;
   }

   public boolean isGui()
   {
      return isGui;
   }

   public void setGui(boolean isGui)
   {
      this.isGui = isGui;
   }
}
