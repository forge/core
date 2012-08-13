/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.Queue;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;

/**
 * Parses named value options such as:
 * <p/>
 * <code>[command] {--option=value}</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NamedValueOptionParser implements CommandParser
{

   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      String currentToken = tokens.peek();
      if (currentToken.startsWith("--"))
      {
         try
         {
            currentToken = currentToken.substring(2);
            if (command.hasOption(currentToken))
            {
               OptionMetadata option = command.getNamedOption(currentToken);
               tokens.remove();

               if (!option.isBoolean())
               {
                  String value = null;
                  if (!tokens.isEmpty())
                  {
                     String nextToken = tokens.peek();
                     if (!nextToken.startsWith("--"))
                     {
                        value = nextToken;
                        tokens.remove(); // increment the chain of tokens
                     }
                  }
                  ctx.put(option, Strings.stripQuotes(value), Strings.stripQuotes(value)); // add the value, should we
                                                                                           // return
                  // this
                  // as a tuple instead?
               }
            }
         }
         catch (IllegalArgumentException e)
         {
            ctx.addWarning("No such option [--" + currentToken + "] for command [" + command + "].");
         }
      }
      return ctx;
   }

}
