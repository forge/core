/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.Queue;

import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;

/**
 * Parses named boolean options such as:
 * <p/>
 * <code>[command] {--toggle}</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
public class NamedBooleanOptionParser implements CommandParser
{
   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      String currentToken = tokens.peek();
      if (currentToken.matches("--?\\S+"))
      {
         if ((currentToken.length() > 1) && (currentToken.charAt(1) != '-'))
         {
            boolean matched = false;
            for (int i = 1; i < currentToken.length(); i++)
            {
               String shortOption = currentToken.substring(i, i + 1);

               try
               {
                  if (command.hasShortOption(shortOption))
                  {
                     processOption(ctx, tokens, command, shortOption, true);
                     matched = true;
                  }
               }
               catch (IllegalArgumentException e)
               {
                  ctx.addWarning("No such option [-" + shortOption + "] for command [" + command + "].");
               }
            }
            if (matched)
               tokens.remove();
         }
         else
         {
            currentToken = currentToken.substring(2);

            try
            {
               if (command.hasOption(currentToken))
               {
                  processOption(ctx, tokens, command, currentToken, false);
               }
            }
            catch (IllegalArgumentException e)
            {
               ctx.addWarning("No such option [--" + currentToken + "] for command [" + command + "].");
            }
         }
      }
      return ctx;
   }

   private static void processOption(final CommandParserContext ctx, final Queue<String> tokens,
            final CommandMetadata command, final String currentToken,
            final boolean shortOption)
   {
      OptionMetadata option = command.getNamedOption(currentToken);

      if (option.isBoolean())
      {
         String value = "true";
         if (!tokens.isEmpty())
         {
            if (!shortOption)
            {
               tokens.remove();
            }
            String nextToken = tokens.peek();
            if (!option.isFlagOnly() && (nextToken != null) && nextToken.matches("true|false"))
            {
               value = nextToken;
               tokens.remove();
            }
         }

         ctx.put(option, value, null);
      }
   }
}
