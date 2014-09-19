/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.SingleCommandController;
import org.jboss.forge.addon.ui.impl.context.UIBuilderImpl;
import org.jboss.forge.addon.ui.impl.context.UIExecutionContextImpl;
import org.jboss.forge.addon.ui.impl.context.UIValidationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.output.UIMessage.Severity;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class SingleCommandControllerImpl extends AbstractCommandController implements SingleCommandController
{
   private UIBuilderImpl uiBuilder;
   private ConverterFactory converterFactory;

   SingleCommandControllerImpl(AddonRegistry addonRegistry, UIRuntime runtime, UICommand command, UIContext context)
   {
      super(addonRegistry, runtime, command, context);
      this.converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
   }

   @Override
   public void initialize() throws Exception
   {
      if (!isInitialized())
      {
         uiBuilder = new UIBuilderImpl(context);
         initialCommand.initializeUI(uiBuilder);
      }
   }

   @Override
   public boolean isInitialized()
   {
      return (uiBuilder != null);
   }

   @Override
   public Result execute() throws Exception
   {
      assertInitialized();
      assertValid();
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIPrompt prompt = runtime.createPrompt(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor, prompt);
      if (progressMonitor.isCancelled())
      {
         return null;
      }

      Set<CommandExecutionListener> listeners = new LinkedHashSet<>();
      listeners.addAll(context.getListeners());
      for (CommandExecutionListener listener : addonRegistry
               .getServices(CommandExecutionListener.class))
      {
         listeners.add(listener);
      }
      firePreCommandExecuted(executionContext, listeners, initialCommand);
      try
      {
         Result result = initialCommand.execute(executionContext);
         firePostCommandExecuted(executionContext, listeners, initialCommand, result);
         return result;
      }
      catch (Exception e)
      {
         firePostCommandFailure(executionContext, listeners, initialCommand, e);
         throw e;
      }
   }

   @Override
   public boolean canExecute()
   {
      return isInitialized() && isValid();
   }

   @Override
   public Map<String, InputComponent<?, ?>> getInputs()
   {
      assertInitialized();
      return uiBuilder.getInputs();
   }

   @SuppressWarnings("unchecked")
   @Override
   public CommandController setValueFor(String inputName, Object value)
   {
      InputComponent<?, ?> input = getInputs().get(inputName);
      if (input == null)
      {
         throw new IllegalArgumentException("Input named '" + inputName + "' does not exist");
      }
      InputComponents.setValueFor(getConverterFactory(), (InputComponent<?, Object>) input, value);
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      InputComponent<?, ?> input = getInputs().get(inputName);
      return InputComponents.getValueFor(input);
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return initialCommand.getMetadata(context);
   }

   @Override
   public boolean isEnabled()
   {
      return initialCommand.isEnabled(context);
   }

   @Override
   public List<UIMessage> validate()
   {
      assertInitialized();
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      for (InputComponent<?, ?> inputComponent : getInputs().values())
      {
         validationContext.setCurrentInputComponent(inputComponent);
         inputComponent.validate(validationContext);
      }
      validationContext.setCurrentInputComponent(null);
      if (!containsErrorMessage(validationContext.getMessages()))
      {
         initialCommand.validate(validationContext);
      }
      return validationContext.getMessages();
   }

   @Override
   public boolean isValid()
   {
      List<UIMessage> messages = validate();
      return !containsErrorMessage(messages);
   }

   private boolean containsErrorMessage(List<UIMessage> messages)
   {
      for (UIMessage message : messages)
      {
         if (message.getSeverity() == Severity.ERROR)
            return true;
      }
      return false;
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @Override
   public UICommand getCommand()
   {
      return initialCommand;
   }

   protected ConverterFactory getConverterFactory()
   {
      return converterFactory;
   }
}
