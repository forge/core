/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.util;

/**
 * Sets the current thread returned in {@link Thread#currentThread()} to the specified name while running this Runnable
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ThreadNamingRunnable implements Runnable
{

   private String threadName;
   private Runnable runnable;

   public ThreadNamingRunnable(String threadName, Runnable runnable)
   {
      super();
      this.threadName = threadName;
      this.runnable = runnable;
   }

   @Override
   public void run()
   {
      Thread currentThread = Thread.currentThread();
      String currentThreadName = currentThread.getName();
      try
      {
         currentThread.setName(threadName);
         runnable.run();
      }
      finally
      {
         currentThread.setName(currentThreadName);
      }
   }
}
