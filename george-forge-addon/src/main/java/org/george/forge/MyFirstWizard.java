package org.george.forge;

import org.jboss.forge.container.services.Remote;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.impl.UIInputImpl;

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

@Remote
public class MyFirstWizard implements UICommand
{

   private UIInput<String> firstName = new UIInputImpl<String>("firstName", String.class);

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
