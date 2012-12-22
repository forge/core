package org.jboss.forge.container;

import java.util.concurrent.Future;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonThread
{
   private Future<?> future;
   private AddonRunnable runnable;

   public AddonThread(Future<?> future, AddonRunnable runnable)
   {
      this.future = future;
      this.runnable = runnable;
   }

   public Future<?> getFuture()
   {
      return future;
   }

   public AddonRunnable getRunnable()
   {
      return runnable;
   }

   @Override
   public String toString()
   {
      return "AddonThread [future=" + future + ", runnable=" + runnable + "]";
   }

}
