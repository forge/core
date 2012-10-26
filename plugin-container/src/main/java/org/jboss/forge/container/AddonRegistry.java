package org.jboss.forge.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

@Typed()
public class AddonRegistry
{
   private Set<Addon> addons = Sets.getConcurrentSet();

   /**
    * Global Addon registry.
    */
   static AddonRegistry registry = new AddonRegistry();

   @Produces
   @Typed(AddonRegistry.class)
   @Singleton
   public static AddonRegistry produceGlobalAddonRegistry()
   {
      return AddonRegistry.registry;
   }

   public boolean register(Addon addon)
   {
      if (addons.contains(addon))
      {
         throw new IllegalArgumentException("Addon [" + addon + "] already registered.");
      }
      return addons.add(addon);
   }

   public Map<ClassLoader, ServiceRegistry> getServices()
   {
      Map<ClassLoader, ServiceRegistry> services = new HashMap<ClassLoader, ServiceRegistry>();
      for (Addon addon : addons)
      {
         services.put(addon.getClassLoader(), addon.getServiceRegistry());
      }
      return services;
   }

   public boolean removeServices(ClassLoader classLoader) throws IllegalArgumentException
   {
      for (Addon addon : addons)
      {
         if (addon.getClassLoader().equals(classLoader))
         {
            return addons.remove(addon);
         }
      }
      return false;
   }

   public boolean remove(Addon addon)
   {
      return addons.remove(addon);
   }

   @Override
   public String toString()
   {
      return addons.toString();
   }

   public Set<Addon> getRegisteredAddons()
   {
      return Collections.unmodifiableSet(addons);
   }
}
