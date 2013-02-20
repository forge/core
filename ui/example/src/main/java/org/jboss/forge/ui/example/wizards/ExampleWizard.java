/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.base.UICommandMetadataBase;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UISelection;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.wizard.UIWizard;

public class ExampleWizard implements UIWizard
{

   @Inject
   private UIInput<String> firstName;

   @Inject
   private UIInput<Boolean> goToLastStep;

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("Wizard", "This is the First screen of the Wizard",
               UICommandMetadataBase.getDocLocationFor(getClass()));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(firstName).add(goToLastStep);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      System.out.println("Validate");
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UISelection<?> selection = context.getInitialSelection();
      return selection != null;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      if (goToLastStep.getValue() != null && goToLastStep.getValue())
      {
         return Results.navigateTo(ExampleStepTwo.class);
      }
      return Results.navigateTo(ExampleStepOne.class);
   }
}
