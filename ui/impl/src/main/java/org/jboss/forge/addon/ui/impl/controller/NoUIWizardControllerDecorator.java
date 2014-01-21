/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;
import java.util.Map;

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
   private final WizardCommandController controller;

   public NoUIWizardControllerDecorator(WizardCommandController controller)
   {
      this.controller = controller;
   }

   @Override
   public void initialize() throws Exception
   {
      controller.initialize();
      if (this.controller.getInputs().isEmpty() && controller.canMoveToNextStep())
      {
         moveToNextStepWithUI();
      }
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

   @Override
   public boolean canMoveToNextStep()
   {
      return controller.canMoveToNextStep();
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      return controller.canMoveToPreviousStep();
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      moveToNextStepWithUI();
      return this;
   }

   @Override
   public WizardCommandController previous() throws Exception
   {
      moveToPreviousStepWithUI();
      return this;
   }

   private void moveToNextStepWithUI() throws Exception
   {
      do
      {
         controller.next().initialize();
      }
      while (controller.getInputs().isEmpty() && controller.canMoveToNextStep());
   }

   private void moveToPreviousStepWithUI() throws Exception
   {
      do
      {
         controller.previous();
      }
      while (controller.getInputs().isEmpty() && controller.canMoveToPreviousStep());
   }

}
