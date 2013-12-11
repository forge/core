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

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
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
   private final CommandControllerFactory controllerFactory;

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

   public WizardCommandControllerImpl(AddonRegistry addonRegistry, UIRuntime runtime,
            UIWizard initialCommand, CommandControllerFactory controllerFactory)
   {
      super(addonRegistry, runtime, initialCommand);
      this.controllerFactory = controllerFactory;
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
      for (CommandController controller : flow)
      {
         result = controller.execute();
      }
      return result;
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
         return (next == null || !subflow.isEmpty());
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
      return true;
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      // If the limit was reached
      if (flowPointer + 1 == flow.size())
      {
         final Class<? extends UICommand> next;
         Class<? extends UICommand>[] result = getNextFrom(getCurrentCommandController().getCommand());
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
               subflow.add(result[i]);
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
      return controllerFactory.createSingleController(command, runtime);
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
