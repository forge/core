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
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.ui.test.CommandListener;
import org.jboss.forge.ui.test.CommandTester;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.UIValidationContextImpl;
import org.jboss.forge.ui.test.impl.command.UIBuilderImpl;

/**
 * This class eases the testing of Wizards
 * 
 */
@Vetoed
public class CommandTesterImpl<C extends UICommand> implements CommandTester<C>
{
   private final AddonRegistry addonRegistry;

   private final UIBuilderImpl builder;

   private final UIContextImpl context;

   public CommandTesterImpl(Class<C> commandClass, AddonRegistry addonRegistry, UIContextImpl contextImpl)
            throws Exception
   {
      this.addonRegistry = addonRegistry;
      this.context = contextImpl;
      builder = createBuilder(commandClass);
   }

   @Override
   public void setInitialSelection(Resource<?>... selection)
   {
      context.setInitialSelection(selection);
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
   public void execute(CommandListener listener) throws Exception
   {
   // validate before execute
      List<String> errors = getValidationErrors();
      if (!errors.isEmpty())
      {
         throw new IllegalStateException(errors.toString());
      }
      // All good. Hit it !
      UICommand comand = builder.getCommand();
      Result result = comand.execute(context);
      if (listener != null)
      {
         listener.commandExecuted(comand, result);
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

   private ConverterFactory getConverterFactory()
   {
      return addonRegistry.getServices(ConverterFactory.class).get();
   }
}
