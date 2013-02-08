package org.jboss.forge.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UISelection;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;
import org.jboss.forge.ui.wizard.UIWizard;

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

public class ExampleWizard implements UIWizard
{

   @Inject
   private UIInput<String> firstName;

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("Wizard", "Exit the shell");
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
      context.getUIBuilder().add(firstName);
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
   public Class<? extends UIWizard> getSuccessor()
   {
      return ExampleStepOne.class;
   }

}
