package org.jboss.forge.ui;

import javax.inject.Inject;

import org.jboss.forge.ui.base.UICommandMetadataBase;
import org.jboss.forge.ui.util.Categories;
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
      return new UICommandMetadataBase(MyFirstWizard.class.getName(), "generic test wizard",
               Categories.create("Example"));
   }

   @Override
   public Class<? extends UIWizard> getSuccessor()
   {
      return null;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

}