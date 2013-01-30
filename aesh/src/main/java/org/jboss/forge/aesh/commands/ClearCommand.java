/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.console.Console;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;

import javax.inject.Inject;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ClearCommand implements UICommand
{
   private Console console;

   @Inject
   private UIInput<String> clear;

   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("clear", "Clear the console");
   }

   public ClearCommand setConsole(Console console)
   {
      this.console = console;
      return this;
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
      clear.setLabel("clear");
      clear.setRequired(true);
      context.getUIBuilder().add(clear);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      console.clear();
      return Results.success("");
   }

}
