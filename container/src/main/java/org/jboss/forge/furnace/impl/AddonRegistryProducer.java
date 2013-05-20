package org.jboss.forge.furnace.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.furnace.addons.AddonRegistry;

@Singleton
public class AddonRegistryProducer
{
   private AddonRegistry registry;

   @Produces
   @Typed(AddonRegistry.class)
   @Singleton
   public AddonRegistry produceGlobalAddonRegistry()
   {
      return registry;
   }

   public void setRegistry(AddonRegistry registry)
   {
      this.registry = registry;
   }
}
