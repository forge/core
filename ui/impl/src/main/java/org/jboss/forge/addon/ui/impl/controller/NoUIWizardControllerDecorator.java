/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.result.Result;

/**
 * This decorator supresses the pages where no {@link InputComponent} is provided
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NoUIWizardControllerDecorator implements WizardCommandController
{
   private final WizardCommandControllerImpl controller;
   private static final Logger log = Logger.getLogger(NoUIWizardControllerDecorator.class.getName());

   public NoUIWizardControllerDecorator(WizardCommandControllerImpl controller)
   {
      this.controller = controller;
   }

   @Override
   public void initialize() throws Exception
   {
      controller.initialize();
      if (this.controller.getInputs().isEmpty() && canMoveToNextStep())
      {
         next();
      }
   }

   @Override
   public boolean canMoveToNextStep()
   {
      boolean result = false;
      if (controller.canMoveToNextStep())
      {
         int flowPointer = controller.getFlowPointer();
         try
         {
            while (!result && controller.canMoveToNextStep())
            {
               controller.next().initialize();
               if (!controller.getInputs().isEmpty())
               {
                  result = true;
               }
            }
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Error while navigating to next step", e);
         }
         finally
         {
            controller.setFlowPointer(flowPointer);
         }
      }
      return result;
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      boolean result = false;
      if (controller.canMoveToPreviousStep())
      {
         int flowPointer = controller.getFlowPointer();
         try
         {
            while (!result && controller.canMoveToPreviousStep())
            {
               controller.previous();
               if (!controller.getInputs().isEmpty())
               {
                  result = true;
               }
            }
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Error while navigating to previous step", e);
         }
         finally
         {
            controller.setFlowPointer(flowPointer);
         }
      }
      return result;
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      int pointer = controller.getFlowPointer();
      while (controller.canMoveToNextStep())
      {
         controller.next().initialize();
         if (!controller.getInputs().isEmpty())
         {
            pointer = controller.getFlowPointer();
            break;
         }
      }
      controller.setFlowPointer(pointer);
      return this;
   }

   @Override
   public WizardCommandController previous() throws Exception
   {
      int pointer = controller.getFlowPointer();
      while (controller.canMoveToPreviousStep())
      {
         controller.previous();
         if (!controller.getInputs().isEmpty())
         {
            pointer = controller.getFlowPointer();
            break;
         }
      }
      controller.setFlowPointer(pointer);
      return this;
   }

   @Override
   public List<UICommandMetadata> getWizardStepsMetadata()
   {
      return controller.getWizardStepsMetadata();
   }

   @Override
   public boolean isInitialized()
   {
      return controller.isInitialized();
   }

   @Override
   public Result execute() throws Exception
   {
      return controller.execute();
   }

   @Override
   public List<UIMessage> validate()
   {
      return controller.validate();
   }

   @Override
   public List<UIMessage> validate(InputComponent<?, ?> input)
   {
      return controller.validate(input);
   }

   @Override
   public boolean isValid()
   {
      return controller.isValid();
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      controller.setValueFor(inputName, value);
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      return controller.getValueFor(inputName);
   }

   @Override
   public Map<String, InputComponent<?, ?>> getInputs()
   {
      return controller.getInputs();
   }

   @Override
   public InputComponent<?, ?> getInput(String inputName)
   {
      return getInputs().get(inputName);
   }

   @Override
   public boolean hasInput(String inputName)
   {
      return getInputs().containsKey(inputName);
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return controller.getMetadata();
   }

   @Override
   public boolean isEnabled()
   {
      return controller.isEnabled();
   }

   @Override
   public UICommand getCommand()
   {
      return controller.getCommand();
   }

   @Override
   public UIContext getContext()
   {
      return controller.getContext();
   }

   @Override
   public boolean canExecute()
   {
      return controller.canExecute();
   }

   @Override
   public void close() throws Exception
   {
      controller.close();
   }

   @Override
   public UICommandMetadata getInitialMetadata()
   {
      return controller.getInitialMetadata();
   }
}
