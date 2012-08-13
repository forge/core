/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.mvel2.util.StringAppender;

/**
 * A simple port of the Unix grep command.
 * 
 * @author Mike Brock .
 */
@Alias("grep")
@Topic("File & Resources")
@Help("print lines matching a pattern")
public class GrepPlugin implements Plugin
{
   @DefaultCommand
   public void run(
            @PipeIn final InputStream pipeIn,
            @Option(name = "ignore-case", shortName = "i", help = "ignore case distinctions in both patterns and input", flagOnly = true) boolean ignoreCase,
            @Option(name = "regexp", shortName = "e", help = "match using a regular expression") String regExp,
            @Option(description = "PATTERN") String pattern,
            @Option(description = "FILE ...") Resource<?>[] resources,
            final PipeOut pipeOut
            ) throws IOException
   {
      Pattern matchPattern;
      if (regExp != null)
      {
         if (ignoreCase)
         {
            regExp = regExp.toLowerCase();
         }
         matchPattern = Pattern.compile(regExp);
      }
      else if (pattern == null)
      {
         throw new RuntimeException("you must specify a pattern");
      }
      else
      {
         if (ignoreCase)
         {
            pattern = pattern.toLowerCase();
         }
         matchPattern = Pattern.compile(".*" + pattern + ".*");
      }

      if (resources != null)
      {
         for (Resource<?> r : resources)
         {
            InputStream inputStream = r.getResourceInputStream();
            try
            {
               match(inputStream, matchPattern, pipeOut, ignoreCase);
            }
            finally
            {
               inputStream.close();
            }
         }
      }
      else if (pipeIn != null)
      {
         match(pipeIn, matchPattern, pipeOut, ignoreCase);
      }
      else
      {
         throw new RuntimeException("arguments required");
      }
   }

   private void match(InputStream instream, Pattern pattern, PipeOut out, boolean caseInsensitive) throws IOException
   {
      StringAppender buf = new StringAppender();

      int c;
      while ((c = instream.read()) != -1)
      {
         switch (c)
         {
         case '\r':
         case '\n':
            String s = caseInsensitive ? buf.toString().toLowerCase() : buf.toString();

            if (pattern.matcher(s).matches())
            {
               out.println(s);
            }
            buf.reset();
            break;
         default:
            buf.append((char) c);
            break;
         }
      }
   }
}
