/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

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
            if (res instanceof FileResource)
            {
               InputStream istream = null;
               try
               {
                  istream = new BufferedInputStream(new FileInputStream(res.getFullyQualifiedName()));
                  lastBuf = writeOutToConsole(istream, out);
               }
               catch (IOException e)
               {
                  throw new RuntimeException("error opening file: " + res.getName());
               }
               finally
               {
                  if (istream != null)
                  {
                     istream.close();
                  }
               }
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
