/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
      getCurrentController().initialize();
   }

   @Override
   public boolean isInitialized()
   {
      return getCurrentController().isInitialized();
   }

   @Override
   public Result execute() throws Exception
   {
      Result result = null;

      assertInitialized();
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor);
      Set<CommandExecutionListener> listeners = new LinkedHashSet<>();
      listeners.addAll(context.getListeners());
      for (CommandExecutionListener listener : addonRegistry
               .getServices(CommandExecutionListener.class))
      {
         listeners.add(listener);
      }
      assertValid();
      for (CommandController controller : flow)
      {
         if (progressMonitor.isCancelled())
         {
            break;
         }
         UICommand command = controller.getCommand();
         try
         {
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
         catch (Exception e)
         {
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandFailure(command, executionContext, e);
            }

            throw e;
         }
      }
      return result;
   }

   @Override
   public List<UIValidationMessage> validate()
   {
      return getCurrentController().validate();
   }

   @Override
   public boolean isValid()
   {
      return getCurrentController().isValid();
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      getCurrentController().setValueFor(inputName, value);
      removeSubsequentPages();
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      return getCurrentController().getValueFor(inputName);
   }

   @Override
   public Map<String, InputComponent<?, ?>> getInputs()
   {
      return getCurrentController().getInputs();
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return getCurrentController().getMetadata();
   }

   @Override
   public UICommandMetadata getInitialMetadata()
   {
      return flow.get(0).getMetadata();
   }

   @Override
   public boolean isEnabled()
   {
      return getCurrentController().isEnabled();
   }

   @Override
   public UICommand getCommand()
   {
      return getCurrentController().getCommand();
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
         Class<? extends UICommand>[] next = getNextFrom(getCurrentController().getCommand());
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
         CommandController currentController = getCurrentController();
         Class<? extends UICommand>[] result = getNextFrom(currentController.getCommand());
         if (result == null)
         {
            if (subflow.isEmpty())
            {
               throw new IllegalStateException("No next step found");
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
      if (!canMoveToPreviousStep())
      {
         throw new IllegalStateException("No previous step found");
      }
      flowPointer--;
      return this;
   }

   private CommandController getCurrentController()
   {
      return flow.get(flowPointer);
   }

//   private CommandController getNextController()
//   {
//      int idx = flowPointer + 1;
//      return (idx < flow.size()) ? flow.get(idx) : null;
//   }

   private CommandController createControllerFor(Class<? extends UICommand> commandClass) throws Exception
   {
      UICommand command = addonRegistry.getServices(commandClass).get();
      return createControllerFor(context, command);
   }

   private CommandController createControllerFor(UIContext context, UICommand command)
   {
      return controllerFactory.createSingleController(context, command, runtime);
   }

   private Class<? extends UICommand>[] getNextFrom(UICommand command) throws Exception
   {
      Class<? extends UICommand>[] result = null;
      if (command instanceof UIWizard)
      {
         NavigationResult next = ((UIWizard) command).next(context);
         if (next != null)
            result = next.getNext();
      }
      return result;
   }

   /**
    * Remove stale pages
    */
   private void removeSubsequentPages()
   {
      flow.subList(flowPointer + 1, flow.size()).clear();
      subflow.clear();
   }

}
