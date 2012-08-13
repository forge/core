/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class OrderedValueVarargsOptionParser implements CommandParser
{
   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      try
      {
         OptionMetadata option = command.getOrderedOptionByIndex(ctx.getOrderedParamCount());
         if (option.isVarargs())
         {
            List<String> args = new ArrayList<String>();
            String lastToken = null;
            // gobble unless we hit a named token
            while (!tokens.isEmpty())
            {
               lastToken = tokens.peek();
               if (lastToken.startsWith("-") && command.hasOption(lastToken.replaceAll("^--?", "")))
               {
                  break;
               }
               lastToken = tokens.remove();
               lastToken = Strings.stripQuotes(lastToken);
               args.add(lastToken);
            }
            ctx.put(option, args.toArray(new String[args.size()]), Strings.stripQuotes(lastToken));
            ctx.incrementParmCount();
         }
      }
      catch (IllegalArgumentException e)
      {
         ctx.addWarning("The command [" + command + "] takes ["
                  + command.getNumOrderedOptions() + "] unnamed argument(s), but found ["
                  + (ctx.getOrderedParamCount() + 1)
                  + "].");
      }
      return ctx;
   }

}
