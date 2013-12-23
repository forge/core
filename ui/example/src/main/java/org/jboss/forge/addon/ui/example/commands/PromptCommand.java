/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.example.commands;

import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PromptCommand extends AbstractUICommand
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {

   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(PromptCommand.class).name("prompt").description("Prompts for Information");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIProvider provider = context.getUIContext().getProvider();
      UIOutput output = provider.getOutput();
      UIPrompt prompt = provider.getPrompt();
      boolean answer = prompt.promptBoolean("Do you love Forge 2?");
      output.out().println("You answered: " + answer);
      return null;
   }

}
