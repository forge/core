/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.forge.resource.DirectoryResource;

/**
 * Executes native system commands.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NativeSystemCall
{
   /**
    * Execute a native system command as if it were run from the given path.
    * 
    * @param command the system command to execute
    * @param parms the command parameters
    * @param out a print writer to which command output will be streamed
    * @param path the path from which to execute the command
    * 
    * @return 0 on successful completion, any other return code denotes failure
    */
   public static int execFromPath(final String command, final String[] parms, final OutputStream out,
            final DirectoryResource path) throws IOException
   {
      try
      {
         String[] commandTokens = parms == null ? new String[1] : new String[parms.length + 1];
         commandTokens[0] = command;

         if (commandTokens.length > 1)
         {
            System.arraycopy(parms, 0, commandTokens, 1, parms.length);
         }

         ProcessBuilder builder = new ProcessBuilder(commandTokens);
         builder.directory(path.getUnderlyingResourceObject());
         builder.redirectErrorStream(true);
         Process p = builder.start();

         InputStream stdout = p.getInputStream();

         Thread outThread = new Thread(new Receiver(stdout, out));
         outThread.start();
         outThread.join();

         return p.waitFor();

      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
         return -1;
      }
   }

   /**
    * Execute the given system command
    * 
    * @return 0 on successful completion, any other return code denotes failure
    */
   public static void exec(final boolean wait, final String command, final String... parms)
            throws IOException
   {
      String[] commandTokens = parms == null ? new String[1] : new String[parms.length + 1];
      commandTokens[0] = command;

      if (commandTokens.length > 1)
      {
         System.arraycopy(parms, 0, commandTokens, 1, parms.length);
      }

      Runtime.getRuntime().exec(commandTokens, null);
   }

   /**
    * Handles streaming output from executed Processes
    */
   private static class Receiver implements Runnable
   {
      private final InputStream in;
      private final OutputStream out;

      public Receiver(InputStream in, OutputStream out)
      {
         this.in = in;
         this.out = out;
      }

      @Override
      public void run()
      {
         try
         {
            byte[] buf = new byte[1024];
            int read;
            while ((read = in.read(buf)) != -1)
            {
               out.write(buf, 0, read);
            }

         }
         catch (IOException e)
         {
            throw new RuntimeException("Error reading input from child process", e);
         }
      }
   }
}
