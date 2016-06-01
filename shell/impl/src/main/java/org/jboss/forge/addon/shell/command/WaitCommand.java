/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Waits for input
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class WaitCommand extends AbstractShellCommand
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // no arguments
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("wait")
               .description("Wait for ENTER.");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      context.getPrompt().prompt("Press <ENTER> to continue...");
      return Results.success();
   }

   @Command(value = "wait", help = "Wait for ENTER.", enabled = NonGUIEnabledPredicate.class)
   public void wait(UIPrompt prompt)
   {
      prompt.prompt("Press <ENTER> to continue...");
   }
}
