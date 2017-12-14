/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;

import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractWizardControllerDecorator extends AbstractCommandControllerDecorator
         implements WizardCommandController
{
   protected AbstractWizardControllerDecorator(WizardCommandController delegate)
   {
      super(delegate);
   }

   /**
    * @return the delegate
    */
   @Override
   protected WizardCommandController getDelegate()
   {
      return (WizardCommandController) super.getDelegate();
   }

   @Override
   public UICommandMetadata getInitialMetadata()
   {
      return getDelegate().getInitialMetadata();
   }

   @Override
   public boolean canMoveToNextStep()
   {
      return getDelegate().canMoveToNextStep();
   }

   @Override
   public boolean canMoveToPreviousStep()
   {
      return getDelegate().canMoveToPreviousStep();
   }

   @Override
   public WizardCommandController next() throws Exception
   {
      getDelegate().next();
      return this;
   }

   @Override
   public WizardCommandController previous() throws Exception
   {
      getDelegate().previous();
      return this;
   }

   @Override
   public List<UICommandMetadata> getWizardStepsMetadata()
   {
      return getDelegate().getWizardStepsMetadata();
   }
}