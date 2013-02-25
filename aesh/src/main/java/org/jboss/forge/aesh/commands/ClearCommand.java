/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import javax.inject.Inject;

import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ClearCommand implements UICommand
{

   @Inject
   private UIInput<String> clear;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("clear").description("Clear the console");
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
       if(context instanceof ShellContext) {
          ((ShellContext) context).getShell().getConsole().clear();
       }
      return Results.success("");
   }

}
