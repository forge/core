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
import org.jboss.forge.ui.wizard.UIWizardStep;

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

public class ExampleStepOne implements UIWizardStep
{

   @Inject
   private UIInput<String> address;

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("Step 1", "Enter your Address");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      address.setLabel("Address:").setRequired(true);
      builder.add(address);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String value = address.getValue();
      if (value != null && !value.isEmpty() && !value.matches("[a-zA-Z0-9, ]+"))
      {
         context.addValidationError(address, "Address contains invalid characters");
      }
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      System.out.println("Step 1 Address: " + address.getValue());
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
      return Results.navigateTo(ExampleStepTwo.class);
   }
}
