package org.jboss.forge.ui;

import javax.inject.Inject;

import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Categories;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizard;

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

public class MyFirstWizard implements UIWizard
{
   @Inject
   private UIInput<String> firstName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(firstName);
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
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(MyFirstWizard.class).description("generic test wizard")
               .category(Categories.create("Example"));
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

}