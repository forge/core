package org.jboss.forge.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.resource.DirectoryResource;
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

public class ExampleStepTwo implements UIWizardStep
{

   @Inject
   private UIInput<DirectoryResource> location;

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("Step 2", "Select a folder");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      location.setLabel("Location:");
      builder.add(location);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      System.out.println("Validate Step Two");
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      System.out.println("Step Two Location: " + location.getValue());
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
      return null;
   }
}
