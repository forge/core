/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.shell.events.CommandExecuted;

/**
 * Display a "Please wait" spinner for the user, until cancelled. It is a good idea to wrap usage of {@link Wait} in a
 * try-finally block to ensure that the wait is always completed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@Singleton
public class Wait
{
   private static String[] spinnerChars = new String[] { "/", "-", "\\", "|" };
   private boolean complete = true;
   private Runnable runnable;
   private Thread thread;

   private Shell shell;

   @Inject
   public Wait(final Shell shell)
   {
      this.shell = shell;
   }

   /**
    * Make sure we don't continue to wait after a command has completed.
    */
   void cleanup(@Observes CommandExecuted event)
   {
      stop();
   }

   /**
    * Start waiting, printing the default message.
    */
   public void start()
   {
      start("Please wait");
   }

   /**
    * Start waiting.
    */
   public void start(String message)
   {
      runnable = new Runnable()
      {
         @Override
         public void run()
         {
            int i = 0;
            while (isWaiting())
            {
               shell.print(spinnerChars[i++]);
               try
               {
                  shell.write('\b');
                  Thread.sleep(50);
               }
               catch (InterruptedException e)
               {
                  break;
               }
               if (i == spinnerChars.length)
                  i = 0;
            }
         }
      };

      try
      {
         shell.println();
         shell.print(message + "... ");
         complete = false;
         thread = new Thread(runnable);
         thread.start();
      }
      catch (Exception e)
      {
         stop();
      }
   }

   /**
    * Stop waiting.
    */
   public void stop()
   {
      if (isWaiting())
      {
         shell.println();
      }
      complete = true;
   }

   /**
    * Returns true if the waiting spinner is currently being displayed; otherwise, return false.
    */
   public boolean isWaiting()
   {
      return !complete;
   }
}
