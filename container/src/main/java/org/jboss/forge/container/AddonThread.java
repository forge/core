package org.jboss.forge.container;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonThread
{
   private Thread thread;
   private AddonRunnable runnable;

   public AddonThread(Thread thread, AddonRunnable runnable)
   {
      this.thread = thread;
      this.runnable = runnable;
   }

   public Thread getThread()
   {
      return thread;
   }

   public AddonRunnable getRunnable()
   {
      return runnable;
   }

}
