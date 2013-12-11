/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class WizardCommandControllerImpl extends AbstractCommandController implements WizardCommandController
{

   public WizardCommandControllerImpl(AddonRegistry addonRegistry, UIRuntime contextFactory,
            UIWizard initialCommand)
   {
      super(addonRegistry, contextFactory, initialCommand);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#initialize()
    */
   @Override
   public void initialize() throws Exception
   {
      // TODO Auto-generated method stub

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#isInitialized()
    */
   @Override
   public boolean isInitialized()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#execute()
    */
   @Override
   public Result execute() throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#validate()
    */
   @Override
   public List<UIValidationMessage> validate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#isValid()
    */
   @Override
   public boolean isValid()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#setValueFor(java.lang.String, java.lang.Object)
    */
   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getValueFor(java.lang.String)
    */
   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getInputs()
    */
   @Override
   public List<InputComponent<?, Object>> getInputs()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getInput(java.lang.String)
    */
   @Override
   public InputComponent<?, Object> getInput(String inputName) throws IllegalArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#hasInput(java.lang.String)
    */
   @Override
   public boolean hasInput(String inputName)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#getMetadata()
    */
   @Override
   public UICommandMetadata getMetadata()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.CommandController#isEnabled()
    */
   @Override
   public boolean isEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.AutoCloseable#close()
    */
   @Override
   public void close() throws Exception
   {
      // TODO Auto-generated method stub

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.WizardCommandController#canMoveToNextStep()
    */
   @Override
   public boolean canMoveToNextStep()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.WizardCommandController#canMoveToPreviousStep()
    */
   @Override
   public boolean canMoveToPreviousStep()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.WizardCommandController#next()
    */
   @Override
   public WizardCommandController next() throws IllegalStateException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.WizardCommandController#previous()
    */
   @Override
   public WizardCommandController previous() throws IllegalStateException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.controller.WizardCommandController#canExecute()
    */
   @Override
   public boolean canExecute()
   {
      // TODO Auto-generated method stub
      return false;
   }

}
