/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.impl.context.UIExecutionContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

/**
 * 
 * Implementation for the {@link WizardCommandController} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class WizardCommandControllerImpl extends AbstractCommandController implements WizardCommandController
{
   /**
    * The execution flow
    */
   private final List<CommandController> flow = new ArrayList<>();

   /**
    * If there are any subflows, store here
    */
   private final LinkedList<Class<? extends UICommand>> subflow = new LinkedList<>();

   /**
    * The pointer that this flow is on. Starts with 0
    */
   private int flowPointer = 0;

   private final CommandControllerFactory controllerFactory;

   public WizardCommandControllerImpl(UIContext context, AddonRegistry addonRegistry, UIRuntime runtime,
            UIWizard initialCommand, CommandControllerFactory controllerFactory)
   {
      super(addonRegistry, runtime, initialCommand, context);
      this.controllerFactory = controllerFactory;
      flow.add(createControllerFor(context, initialCommand));
   }

   @Override
   public void initialize() throws Exception
   {
      getCurrentCommandController().initialize();
   }

   @Override
   public boolean isInitialized()
   {
      return getCurrentCommandController().isInitialized();
   }

   @Override
   public Result execute() throws Exception
   {
      Result result = null;

      assertInitialized();
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor);
      Imported<CommandExecutionListener> listeners = addonRegistry.getServices(CommandExecutionListener.class);
      try
      {
         assertValid();
         try
         {
            for (CommandController controller : flow)
            {
               UICommand command = controller.getCommand();
               for (CommandExecutionListener listener : listeners)
               {
                  listener.preCommandExecuted(command, executionContext);
               }
               result = command.execute(executionContext);
               for (CommandExecutionListener listener : listeners)
               {
                  listener.postCommandExecuted(command, executionContext, result);
               }
            }
            return result;
         }
         catch (Exception e)
         {
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandFailure(initialCommand, executionContext, e);
            }
            throw e;
         }
      }
      finally
      {
         for (CommandExecutionListener listener : listeners)
         {
            listeners.release(listener);
         }
      }
   }

   @Override
   public List<UIValidationMessage> validate()
   {
      return getCurrentCommandController().validate();
   }

   @Override
   public boolean isValid()
   {
      return getCurrentCommandController().isValid();
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      getCurrentCommandController().setValueFor(inputName, value);
      // Remove subsequent pages
      flow.subList(flowPointer + 1, flow.size()).clear();
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      return getCurrentCommandController().getValueFor(inputName);
   }

   @Override
   public List<InputComponent<?, ?>> getInputs()
   {
      return getCurrentCommandController().getInputs();
   }

   @Override
   public InputComponent<?, ?> getInput(String inputName) throws IllegalArgumentException
   {
      return getCurrentCommandController().getInput(inputName);
   }

   @Override
   public boolean hasInput(String inputName)
   {
      return getCurrentCommandController().hasInput(inputName);
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return getCurrentCommandController().getMetadata();
   }

   @Override
   public boolean isEnabled()
   {
      return getCurrentCommandController().isEnabled();
   }

   @Override
   public UICommand getCommand()
   {
      return getCurrentCommandController().getCommand();
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @Override
   public boolean canMoveToNextStep()
   {
      try
      {
         // Move only if there is a next step or if there is a subflow set
         Class<? extends UICommand>[] next = getNextFrom(getCurrentCommandController().getCommand());
         return (next != null || !subflow.isEmpty());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      return flowPointer > 0;
   }

   @Override
   public boolean canExecute()
   {
      return !canMoveToNextStep() && isValid();
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      assertInitialized();
      assertValid();
      // If the limit was reached
      if (flowPointer + 1 == flow.size())
      {
         final Class<? extends UICommand> next;
         CommandController currentController = getCurrentCommandController();
         Class<? extends UICommand>[] result = getNextFrom(currentController.getCommand());
         if (result == null)
         {
            if (subflow.isEmpty())
            {
               throw new IllegalStateException("No next page found");
            }
            else
            {
               next = subflow.pop();
            }
         }
         else
         {
            next = result[0];
            for (int i = 1; i < result.length; i++)
            {
               // Save this subflow for later
               subflow.addLast(result[i]);
            }
         }
         CommandController controller = createControllerFor(next);
         flow.add(controller);
      }
      flowPointer++;
      return this;
   }

   @Override
   public WizardCommandController previous() throws IllegalStateException
   {
      assertInitialized();
      if (flowPointer == 0)
      {
         throw new IllegalStateException("No previous page found");
      }
      flowPointer--;
      return this;
   }

   private CommandController getCurrentCommandController()
   {
      return flow.get(flowPointer);
   }

   private CommandController createControllerFor(Class<? extends UICommand> commandClass) throws Exception
   {
      UICommand command = addonRegistry.getServices(commandClass).get();
      return createControllerFor(context, command);
   }

   private CommandController createControllerFor(UIContext context, UICommand command)
   {
      return controllerFactory.createSingleController(context, command, runtime);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getInputNames()
    */
   @Override
   public Set<String> getInputNames()
   {
      return getCurrentCommandController().getInputNames();
   }

   private Class<? extends UICommand>[] getNextFrom(UICommand command) throws Exception
   {
      Class<? extends UICommand>[] result;
      if (command instanceof UIWizard)
      {
         NavigationResult next = ((UIWizard) command).next(context);
         result = next.getNext();
      }
      else
      {
         result = null;
      }
      return result;
   }
}
