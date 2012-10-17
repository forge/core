/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.arquillian.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

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
   public static int execFromPath(final File path, final String command, final String... parms) throws IOException
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
         builder.directory(path);
         builder.redirectErrorStream(true);
         Process p = builder.start();

         InputStream stdout = p.getInputStream();

         Thread outThread = new Thread(new Receiver(stdout, System.out));
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
    * Execute a native system command as if it were run from the given path.
    * 
    * @param command the system command to execute
    * @param parms the command parameters
    * 
    * @return The started {@link Process}
    */
   public static Process exec(final String command, final String... parms) throws IOException
   {
      String[] commandTokens = parms == null ? new String[1] : new String[parms.length + 1];
      commandTokens[0] = command;

      if (commandTokens.length > 1)
      {
         System.arraycopy(parms, 0, commandTokens, 1, parms.length);
      }

      ProcessBuilder builder = new ProcessBuilder(commandTokens);
      builder.redirectErrorStream(true);
      Process p = builder.start();

      InputStream stdout = p.getInputStream();

      Thread outThread = new Thread(new Receiver(stdout, System.out));
      outThread.start();

      return p;
   }

   /**
    * Execute the given system command
    * 
    * @return
    * 
    * @return 0 on successful completion, any other return code denotes failure
    * @throws InterruptedException
    */
   public static Process exec(final boolean wait, final String command, final String... parms)
            throws IOException, InterruptedException
   {
      String[] commandTokens = parms == null ? new String[1] : new String[parms.length + 1];
      commandTokens[0] = command;

      if (commandTokens.length > 1)
      {
         System.arraycopy(parms, 0, commandTokens, 1, parms.length);
      }

      Process process = Runtime.getRuntime().exec(commandTokens, null);
      if (wait)
         process.waitFor();
      return process;
   }

   /**
    * Handles streaming output from executed Processes
    */
   private static class Receiver implements Runnable
   {
      private final InputStream in;
      private final PrintStream out;

      public Receiver(InputStream in, PrintStream out)
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
            // Don't care right now.
         }
      }
   }
}
