/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.WizardExecutionListener;

@Vetoed
public class TestCommandListener extends AbstractCommandExecutionListener implements WizardExecutionListener
{
   boolean isWizard;
   volatile Result result;

   @Override
   public void preWizardExecuted(UIWizard wizard, UIExecutionContext context)
   {
      isWizard = true;
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      if (!isWizard)
      {
         this.result = (result == null) ? Results.success() : result;
      }
   }

   @Override
   public void postCommandFailure(UICommand command, UIExecutionContext context, Throwable failure)
   {
      if (!isWizard)
      {
         this.result = Results.fail("Error encountered during command execution.", failure);
      }
   }

   @Override
   public void postWizardExecuted(UIWizard wizard, UIExecutionContext context, Result result)
   {
      this.result = (result == null) ? Results.success() : result;
   }

   @Override
   public void postWizardFailure(UIWizard wizard, UIExecutionContext context, Throwable failure)
   {
      this.result = Results.fail("Error encountered during command execution.", failure);
   }

   public boolean isExecuted()
   {
      return this.result != null;
   }

   public Result getResult()
   {
      return this.result;
   }

   public void reset()
   {
      this.result = null;
      this.isWizard = false;
   }
}