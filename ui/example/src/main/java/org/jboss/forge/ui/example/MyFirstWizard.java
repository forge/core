package org.jboss.forge.ui.example;

import javax.inject.Inject;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;


/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

@Exported
public class MyFirstWizard implements UICommand
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

}
