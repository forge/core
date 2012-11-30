package org.jboss.forge.container.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.AddonRegistry;

public class AddonRegistryProducer
{
   @Produces
   @Typed(AddonRegistry.class)
   @Singleton
   public static AddonRegistry produceGlobalAddonRegistry()
   {
      return AddonRegistryImpl.INSTANCE;
   }
}
