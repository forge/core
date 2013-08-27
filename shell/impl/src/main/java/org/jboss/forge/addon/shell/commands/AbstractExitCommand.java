/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public abstract class AbstractExitCommand extends AbstractShellCommand implements UICommand
{

   @Inject
   private Furnace forge;

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().name("exit").description("Exit the shell");
   }

   @Override
   public void initializeUI(UIBuilder context) throws Exception
   {
   }

   @Override
   public Result execute(ShellContext context) throws Exception
   {
      forge.stop();
      return Results.success("");
   }
}
