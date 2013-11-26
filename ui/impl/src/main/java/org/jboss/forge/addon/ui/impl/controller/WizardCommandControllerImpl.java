/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
abstract class WizardCommandControllerImpl extends AbstractCommandController implements WizardCommandController
{

   public WizardCommandControllerImpl(AddonRegistry addonRegistry, UIWizard initialCommand)
   {
      super(addonRegistry, initialCommand);
   }

   @Override
   public UIWizard getInitialCommand()
   {
      return (UIWizard) super.getInitialCommand();
   }

   @Override
   public UICommand getCurrentCommand()
   {
      return null;
   }

   @Override
   public void launch() throws Exception
   {

   }

   @Override
   public boolean canFlipToNextPage()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean canFlipToPreviousPage()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean canFinish()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public UICommand next() throws IllegalStateException
   {
      return null;
   }

   @Override
   public UICommand previous() throws IllegalStateException
   {
      return null;
   }

   @Override
   public void valueChanged()
   {
      // TODO Auto-generated method stub

   }

}
