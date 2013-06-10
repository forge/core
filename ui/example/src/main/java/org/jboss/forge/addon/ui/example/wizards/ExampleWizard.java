/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class ExampleWizard implements UIWizard
{

   @Inject
   private UIInput<String> firstName;

   @Inject
   private UIInput<Boolean> showSelectComponents;

   @Inject
   @WithAttributes(label = "Folder Location:")
   private UIInput<DirectoryResource> directory;

   @Inject
   private UIInput<Boolean> goToLastStep;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Wizard").description("This is the First screen of the Wizard");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      firstName.setRequired(true).setRequiredMessage("First Name must be informed !");
      builder.add(firstName).add(showSelectComponents).add(goToLastStep).add(directory);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String value = firstName.getValue();
      if (value != null && !value.isEmpty() && !value.matches("[a-zA-Z]+"))
      {
         context.addValidationError(firstName, "First Name contains invalid characters");
      }
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
      return !selection.isEmpty();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      if (showSelectComponents.getValue() != null && showSelectComponents.getValue())
      {
         return Results.navigateTo(ExampleSelectComponents.class);
      }
      if (goToLastStep.getValue() != null && goToLastStep.getValue())
      {
         return Results.navigateTo(ExampleStepTwo.class);
      }
      return Results.navigateTo(ExampleStepOne.class);
   }
}
