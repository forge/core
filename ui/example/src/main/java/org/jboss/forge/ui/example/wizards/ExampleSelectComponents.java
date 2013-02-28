/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UISelection;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UISelectMany;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizardStep;

public class ExampleSelectComponents implements UIWizardStep
{

   @Inject
   private UISelectMany<Bean> beanChoices;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Step 2").description("Select a folder");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public void validate(UIValidationContext context)
   {
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
      return null;
   }
}
