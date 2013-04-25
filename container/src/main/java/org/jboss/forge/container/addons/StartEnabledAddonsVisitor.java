package org.jboss.forge.container.addons;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.AddonRunnable;
import org.jboss.forge.container.util.Visitor;

public class StartEnabledAddonsVisitor implements Visitor<Addon>
{
   @SuppressWarnings("unused")
   private AddonTree tree;

   private Forge forge;
   private ExecutorService executor;
   private Set<AddonId> enabled;
   private AtomicInteger starting;

   public StartEnabledAddonsVisitor(Forge forge, AddonTree tree, ExecutorService executor, AtomicInteger starting,
            Set<AddonId> enabled)
   {
      this.forge = forge;
      this.tree = tree;
      this.executor = executor;
      this.enabled = enabled;
      this.starting = starting;
   }

   @Override
   public void visit(Addon instance)
   {
      if (enabled.contains(instance.getId()) && instance instanceof AddonImpl)
      {
         AddonImpl addon = (AddonImpl) instance;
         if (addon.canBeStarted())
         {
            if (executor.isShutdown())
            {
               throw new IllegalStateException("Cannot start additional addons once Shutdown has been initiated.");
            }

            Future<Void> result = null;
            if (addon.getRunnable() == null)
            {
               starting.incrementAndGet();
               AddonRunnable runnable = new AddonRunnable(forge, addon);
               result = executor.submit(runnable, null);
               addon.setFuture(result);
               addon.setRunnable(runnable);
               addon.setDirty(false);
            }
         }
      }
   }

}
