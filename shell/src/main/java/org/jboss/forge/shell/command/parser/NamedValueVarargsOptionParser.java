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
 * Parses named varargs options such as:
 * <p/>
 * <code>[command] {--option foo bar baz}</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NamedValueVarargsOptionParser implements CommandParser
{

   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      String currentToken = tokens.peek();
      if (currentToken.startsWith("--"))
      {
         currentToken = currentToken.substring(2);
         if (command.hasOption(currentToken))
         {
            try
            {
               OptionMetadata option = command.getNamedOption(currentToken);
               if (option.isVarargs())
               {
                  tokens.remove();
                  List<String> args = new ArrayList<String>();
                  String rawValue = null;
                  // this has to be the last parameter... gobble the rest
                  while (!tokens.isEmpty())
                  {
                     rawValue = tokens.remove();
                     args.add(Strings.stripQuotes(rawValue));
                  }
                  ctx.put(option, args.toArray(), Strings.stripQuotes(rawValue));
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

}
