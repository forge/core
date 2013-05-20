package org.jboss.forge.furnace.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.jboss.forge.furnace.util.Assert;

public class Threads
{

   public static void sleep(int millis)
   {
      try
      {
         Thread.sleep(millis);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T> Future<T> runAsync(final Callable<T> callable)
   {
      Assert.notNull(callable, "Future task must not be null.");

      ExecutorService executor = Executors.newSingleThreadExecutor();
      FutureTask<T> future = new FutureTask<T>(callable);
      executor.execute(future);
      executor.shutdown();

      return future;
   }

}
