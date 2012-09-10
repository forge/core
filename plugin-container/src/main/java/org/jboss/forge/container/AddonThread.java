package org.jboss.forge.container;

import org.jboss.modules.Module;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonThread
{
   private Module module;
   private Thread thread;
   private AddonRunnable runnable;

   public AddonThread(Module module, Thread thread, AddonRunnable runnable)
   {
      this.module = module;
      this.thread = thread;
      this.runnable = runnable;
   }

   public Module getModule()
   {
      return module;
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
