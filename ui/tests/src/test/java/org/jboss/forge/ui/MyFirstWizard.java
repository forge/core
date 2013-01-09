package org.jboss.forge.ui;

import javax.inject.Inject;

import org.jboss.forge.ui.wizard.UIWizardBegin;
import org.jboss.forge.ui.wizard.UIWizardContext;
import org.jboss.forge.ui.wizard.WizardId;

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

public class MyFirstWizard implements UIWizardBegin
{
   @Inject
   private UIInput<String> firstName;

   public void initializeUI(UIContext context) throws Exception
   {
      context.getUIBuilder().add(firstName);
   }

   public void validate(UIValidationContext context)
   {
      System.out.println("Validate");
   }

   public Result execute(UIContext context) throws Exception
   {
      return Result.success();
   }

   @Override
   public WizardId getId()
   {
      return new WizardId()
      {
         @Override
         public String getName()
         {
            return MyFirstWizard.class.getName();
         }

         @Override
         public String getDescription()
         {
            return "generic test wizard";
         }
      };
   }

   @Override
   public Result next(UIWizardContext context) throws Exception
   {
      throw new IllegalStateException("not implemented");
   }

}