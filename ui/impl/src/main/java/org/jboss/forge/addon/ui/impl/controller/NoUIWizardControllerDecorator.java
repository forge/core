/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * This decorator suppresses the pages where no {@link InputComponent} is provided
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NoUIWizardControllerDecorator extends AbstractWizardControllerDecorator
{
   private static final Logger log = Logger.getLogger(NoUIWizardControllerDecorator.class.getName());

   public NoUIWizardControllerDecorator(WizardCommandControllerImpl controller)
   {
      super(controller);
   }

   @Override
   protected WizardCommandControllerImpl getDelegate()
   {
      return (WizardCommandControllerImpl) super.getDelegate();
   }

   @Override
   public void initialize() throws Exception
   {
      WizardCommandController controller = getDelegate();
      controller.initialize();
      if (controller.getInputs().isEmpty() && canMoveToNextStep())
      {
         next();
      }
   }

   @Override
   public boolean canMoveToNextStep()
   {
      boolean result = false;
      WizardCommandControllerImpl controller = getDelegate();
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
      WizardCommandControllerImpl controller = getDelegate();
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
      WizardCommandControllerImpl controller = getDelegate();
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
      WizardCommandControllerImpl controller = getDelegate();
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
}
