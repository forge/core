/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import javax.inject.Inject;

import org.jboss.aesh.console.Console;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;

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
   public boolean isEnabled(UIContext context)
   {
      return context instanceof ShellContext;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      clear.setLabel("clear");
      clear.setRequired(true);
      builder.add(clear);
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
