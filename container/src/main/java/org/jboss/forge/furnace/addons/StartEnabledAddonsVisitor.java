package org.jboss.forge.furnace.addons;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.impl.AddonImpl;
import org.jboss.forge.furnace.impl.AddonRunnable;
import org.jboss.forge.furnace.util.Visitor;

public class StartEnabledAddonsVisitor implements Visitor<Addon>
{
   @SuppressWarnings("unused")
   private AddonTree tree;

   private Furnace forge;
   private ExecutorService executor;
   private Set<AddonId> enabled;
   private AtomicInteger starting;

   public StartEnabledAddonsVisitor(Furnace forge, AddonTree tree, ExecutorService executor, AtomicInteger starting,
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
