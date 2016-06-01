/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.commands;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * Example of {@link UIPrompt} usage
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PromptCommand
{

   @Command(value = "prompt-boolean", help = "Prompts for information")
   public Result promptBoolean(UIPrompt prompt)
   {
      boolean answer = prompt.promptBoolean("Do you love Forge 2?");
      return Results.success("You answered: " + answer);
   }

   @Command(value = "prompt-boolean-false", help = "Prompts for information")
   public Result promptBooleanFalse(UIPrompt prompt)
   {
      boolean answer = prompt.promptBoolean("Do you love Forge 2?", false);
      return Results.success("You answered: " + answer);
   }

   @Command(value = "prompt-secret", help = "Prompts for information")
   public Result promptSecret(UIPrompt prompt)
   {
      String answer = prompt.promptSecret("Type your password: ");
      return Results.success("You answered: " + answer);
   }

   @Command(value = "prompt", help = "Prompts for information")
   public Result prompt(UIPrompt prompt)
   {
      String answer = prompt.prompt("Type something: ");
      return Results.success("You answered: " + answer);
   }

}
