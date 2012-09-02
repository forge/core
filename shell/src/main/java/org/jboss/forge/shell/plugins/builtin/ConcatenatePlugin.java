/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.Streams;

/**
 * @author Mike Brock .
 */
@Alias("cat")
@Topic("File & Resources")
@Help("Concatenate and print files")
public class ConcatenatePlugin implements Plugin
{
   @DefaultCommand
   public void run(
            @PipeIn final InputStream in, // pipe in
            @Option(description = "path", required = false) Resource<?>[] paths, // params
            final PipeOut out // pipe out
   ) throws IOException
   {
      String lastBuf = null;
      if (in != null)
      {
         lastBuf = writeOutToConsole(in, out);
      }

      if (paths != null)
      {

         for (Resource<?> res : paths)
         {
            InputStream is = null;
            try
            {
               is = res.getResourceInputStream();
               if (is != null)
               {
                  lastBuf = writeOutToConsole(is, out);
               }
            }
            finally
            {
               Streams.closeQuietly(is);
            }
         }
      }

      if (lastBuf == null || lastBuf.charAt(lastBuf.length() - 1) != '\n')
      {
         out.println();
      }
   }

   private static String writeOutToConsole(InputStream istream, PipeOut out) throws IOException
   {
      byte[] buf = new byte[10];
      int read;
      String s = null;
      while ((read = istream.read(buf)) != -1)
      {
         out.print(s = new String(buf, 0, read));
      }

      return s;

   }
}
