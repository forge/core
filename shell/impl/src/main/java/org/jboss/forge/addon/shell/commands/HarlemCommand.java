/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.extensions.harlem.Harlem;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class HarlemCommand extends AbstractShellCommand
{
   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata()
               .name("harlem")
               .description("do you want some harlem?");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(ShellContext context) throws Exception
   {
      Console console = context.getProvider().getConsole();
      Harlem harlem = new Harlem(console);
      console.attachProcess(harlem);
      return Results.success();
   }
}
