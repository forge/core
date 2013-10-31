/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.enhancer;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.SelectComponentEnhancer;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.util.Predicate;

public class MainWizard implements UIWizard
{
   @Inject
   private UIInput<String> firstName;

   @Inject
   private SelectComponentEnhancer enhancer;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      enhancer.registerValueChoiceFilter(Gender.class, new Predicate<Gender>()
      {
         @Override
         public boolean accept(Gender type)
         {
            return type == Gender.FEMALE;
         }
      });

      builder.add(firstName);
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
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(MainWizard.class).description("generic test wizard")
               .category(Categories.create("Example"));
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return Results.navigateTo(NextStepWizard.class);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

}