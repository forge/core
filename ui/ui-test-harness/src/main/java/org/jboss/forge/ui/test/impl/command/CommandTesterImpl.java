/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl.command;

import java.util.List;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.CommandExecutionListener;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.ui.test.CommandTester;
import org.jboss.forge.ui.test.impl.UIBuilderImpl;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.UIValidationContextImpl;

/**
 * This class eases the testing of Wizards
 * 
 */
@Vetoed
public class CommandTesterImpl<C extends UICommand> implements CommandTester<C>
{
   private final AddonRegistry addonRegistry;

   private final UIContextImpl context;
   
   private final Class<C> commandClass;

   private UIBuilderImpl builder;

   public CommandTesterImpl(Class<C> commandClass, AddonRegistry addonRegistry, UIContextImpl contextImpl)
   {
      this.addonRegistry = addonRegistry;
      this.context = contextImpl;
      this.commandClass = commandClass;
   }

   @Override
   public void setInitialSelection(Resource<?>... selection)
   {
      context.setInitialSelection(selection);
   }
   
   @Override 
   public void launch() throws Exception {
      builder = createBuilder(commandClass);
   }

   @Override
   public boolean canExecute()
   {
      return getValidationErrors().isEmpty();
   }

   @Override
   public boolean isValid()
   {
      return getValidationErrors().isEmpty();
   }

   @Override
   public List<String> getValidationErrors()
   {
      UICommand currentCommand = builder.getCommand();
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      currentCommand.validate(validationContext);
      return validationContext.getErrors();
   }

   @Override
   public void execute(CommandExecutionListener listener) throws Exception
   {
      try
      {
         // validate before execute
         List<String> errors = getValidationErrors();
         if (!errors.isEmpty())
         {
            throw new IllegalStateException(errors.toString());
         }
         // All good. Hit it !
         UICommand command = builder.getCommand();
         if (listener != null)
         {
            listener.preCommandExecuted(command, context);
         }
         try
         {
            Result result = command.execute(context);
            if (listener != null)
            {
               listener.postCommandExecuted(command, context, result);
            }
         }
         catch (Exception e)
         {
            if (listener != null)
            {
               listener.postCommandFailure(command, context, e);
            }
            throw e;
         }
      }
      finally
      {
         context.destroy();
      }
   }

   private UIBuilderImpl createBuilder(Class<C> commandClass) throws Exception
   {
      C command = addonRegistry.getServices(commandClass).get();
      UIBuilderImpl builder = new UIBuilderImpl(context, command);
      command.initializeUI(builder);
      return builder;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setValueFor(String property, Object value)
   {
      InputComponent<?, ?> input = builder.getComponentNamed(property);
      if (input == null)
      {
         throw new IllegalArgumentException("Property " + property + " not found for current command dialog.");
      }
      InputComponents.setValueFor(getConverterFactory(), (InputComponent<?, Object>) input, value);
   }

   @Override
   public InputComponent<?, ?> getInputComponent(String property)
   {
      return builder.getComponentNamed(property);
   }

   private ConverterFactory getConverterFactory()
   {
      return addonRegistry.getServices(ConverterFactory.class).get();
   }
   
   @Override
   public boolean isEnabled()
   {
      return builder.getCommand().isEnabled(context);
   }
}
