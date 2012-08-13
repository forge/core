/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.parser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Tokenizer
{
   public Queue<String> tokenize(final String line)
   {
      Queue<String> tokens = new LinkedList<String>();

      // -------------------------------(0-(1------)---(2---))----------
      Matcher matcher = Pattern.compile("'([^']*?)'|\"([^\"]*?)\"|(\\S+)").matcher(line);
      while (matcher.find())
      {
         if (matcher.group(1) != null)
         {
            tokens.add(matcher.group(1));
         }
         if (matcher.group(2) != null)
         {
            tokens.add(matcher.group(2));
         }
         else
         {
            tokens.add(matcher.group());
         }
      }

      return tokens;
   }
}
