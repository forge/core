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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OrderedValueOptionParser implements CommandParser
{

   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      String currentToken = tokens.peek();
      if (command.hasOrderedOptions()
               && !command.hasOption(currentToken.replaceAll("^--?", "")))
      {

         if (currentToken.matches("^--?$") && ctx.isCompleting() && !ctx.isTokenComplete())
         {
            return ctx;
         }

         int numberOrderedParams = ctx.getOrderedParamCount();
         try
         {
            OptionMetadata option = command.getOrderedOptionByIndex(numberOrderedParams);
            if (!option.isVarargs())
            {
               ctx.put(option, Strings.stripQuotes(currentToken), Strings.stripQuotes(tokens.remove()));
               ctx.incrementParmCount();
            }
         }
         catch (IllegalArgumentException e)
         {
            ctx.addWarning("The command [" + command + "] takes ["
                     + command.getNumOrderedOptions() + "] unnamed argument(s), but found ["
                     + (numberOrderedParams + 1)
                     + "].");
         }
      }
      return ctx;
   }

}
