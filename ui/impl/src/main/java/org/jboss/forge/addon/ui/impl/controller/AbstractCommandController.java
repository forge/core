/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.impl.context.UIValidationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.validate.UIValidationListener;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * Base class for {@link CommandController} implementations.
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractCommandController implements CommandController
{
   protected final AddonRegistry addonRegistry;
   protected final UIRuntime runtime;
   protected final UIContext context;
   protected final UICommand initialCommand;

   private static final Logger log = Logger.getLogger(AbstractCommandController.class.getName());

   protected AbstractCommandController(AddonRegistry addonRegistry, UIRuntime runtime,
            UICommand initialCommand, UIContext context)
   {
      this.addonRegistry = addonRegistry;
      this.runtime = runtime;
      this.initialCommand = initialCommand;
      this.context = context;
   }

   protected void assertInitialized()
   {
      if (!isInitialized())
         throw new IllegalStateException("Controller must be initialized.");
   }

   protected void assertValid()
   {
      if (!isValid())
         throw new IllegalStateException("Controller is not in valid state: " + validate());
   }

   @Override
   public List<UIMessage> validate(InputComponent<?, ?> input)
   {
      assertInitialized();
      Assert.notNull(input, "InputComponent must not be null.");
      Assert.isTrue(getInputs().values().contains(input), "InputComponent must belong to this command.");
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      validationContext.setCurrentInputComponent(input);
      // Notify validator listeners
      for (UIValidationListener validator : addonRegistry.getServices(UIValidationListener.class))
      {
         validator.preValidate(validationContext, getCommand(), Collections.<InputComponent<?, ?>> singleton(input));
      }
      input.validate(validationContext);
      for (UIValidationListener validator : addonRegistry.getServices(UIValidationListener.class))
      {
         validator.postValidate(validationContext, getCommand(), Collections.<InputComponent<?, ?>> singleton(input));
      }
      return validationContext.getMessages();
   }

   @Override
   public UIContext getContext()
   {
      return context;
   }

   @Override
   public boolean equals(Object obj)
   {
      boolean result = false;
      if (obj instanceof CommandController)
      {
         CommandController newController = (CommandController) obj;
         // Same class Name
         if (!getCommand().getClass().getName().equals(newController.getCommand().getClass().getName()))
         {
            result = false;
         }
         else if (isInitialized() && newController.isInitialized())
         {
            // Compare inputs
            Set<String> originalInputNames = getInputs().keySet();
            Set<String> newInputNames = newController.getInputs().keySet();
            if (originalInputNames.containsAll(newInputNames) && newInputNames.containsAll(originalInputNames))
            {
               result = true;
            }
         }
      }
      return result;
   }

   protected void firePreCommandExecuted(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners, UICommand command)
   {
      for (CommandExecutionListener listener : listeners)
      {
         try
         {
            listener.preCommandExecuted(command, executionContext);
         }
         catch (Throwable t)
         {
            log.log(Level.SEVERE, "Error while notifying listeners", t);
         }
      }
   }

   protected void firePostCommandFailure(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners, UICommand command, Throwable e)
   {
      for (CommandExecutionListener listener : listeners)
      {
         try
         {
            listener.postCommandFailure(command, executionContext, e);
         }
         catch (Throwable t)
         {
            log.log(Level.SEVERE, "Error while notifying listeners", t);
         }
      }
   }

   protected void firePostCommandExecuted(UIExecutionContext executionContext,
            Set<CommandExecutionListener> listeners, UICommand command, Result currentResult)
   {
      for (CommandExecutionListener listener : listeners)
      {
         try
         {
            listener.postCommandExecuted(command, executionContext, currentResult);
         }
         catch (Throwable t)
         {
            log.log(Level.SEVERE, "Error while notifying listeners", t);
         }
      }
   }
}
