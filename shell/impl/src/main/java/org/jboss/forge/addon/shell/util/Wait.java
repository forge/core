/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import java.io.PrintStream;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Wait implements Runnable, AutoCloseable
{
   private final PrintStream output;
   private volatile boolean alive = true;

   public Wait(PrintStream output)
   {
      this.output = output;
   }

   @Override
   public void run()
   {
      String anim = "|/-\\";

      int index = 0;
      try
      {
         // Only start if the task takes longer than 1 second
         Thread.sleep(1000);
         while (alive)
         {
            this.output.write(("\r" + anim.charAt(index++ % anim.length())).getBytes());
            Thread.sleep(50);
         }
      }
      catch (Exception e)
      {
         // do nothing
      }
   }

   @Override
   public void close() throws Exception
   {
      this.alive = false;
   }
}
