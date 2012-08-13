/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.jboss.forge.shell.command.CommandMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CompositeCommandParser implements CommandParser
{
   List<CommandParser> parsers = new ArrayList<CommandParser>();

   public CompositeCommandParser(final CommandParser... parsers)
   {
      this.parsers = Arrays.asList(parsers);
   }

   @Override
   public CommandParserContext parse(final CommandMetadata command, final Queue<String> tokens,
            final CommandParserContext ctx)
   {
      boolean complete = false;
      CommandParserContext context = ctx;
      while (!complete)
      {
         boolean altered = false;
         for (CommandParser parser : parsers)
         {
            if (tokens.size() == 0)
            {
               complete = true;
               break;
            }

            int size = tokens.size();
            context = parser.parse(command, tokens, context);

            if (size > tokens.size())
            {
               altered = true;
               break;
            }
         }

         if (!altered)
         {
            break;
         }
      }
      return ctx;
   }

}
