/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.impl.context.UIValidationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;
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
      Assert.isTrue(isInitialized(), "Controller must be initialized.");
   }

   protected void assertValid()
   {
      Assert.isTrue(isValid(), "Controller is not in valid state.");
   }

   @Override
   public List<UIValidationMessage> validate(InputComponent<?, Object> input)
   {
      assertInitialized();
      Assert.notNull(input, "InputComponent must not be null.");
      Assert.isTrue(getInputs().contains(input), "InputComponent must belong to this command.");
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      input.validate(validationContext);
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
         if (getCommand().getClass().getName().equals(newController.getCommand().getClass().getName()))
         {
            result = true;
         }
         else if (isInitialized() && newController.isInitialized())
         {
            // Compare inputs
            Set<String> originalInputNames = getInputNames();
            Set<String> newInputNames = newController.getInputNames();
            if (originalInputNames.containsAll(newInputNames) && newInputNames.containsAll(originalInputNames))
            {
               result = true;
            }
         }
      }
      return result;
   }
}
