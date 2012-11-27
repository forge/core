package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

@Typed()
public class AddonRegistryImpl implements AddonRegistry
{
   private Set<Addon> addons = Sets.getConcurrentSet();

   /**
    * Global Addon registry.
    */
   public static AddonRegistryImpl registry = new AddonRegistryImpl();

   public boolean register(Addon addon)
   {
      if (addons.contains(addon))
      {
         throw new IllegalArgumentException("Addon [" + addon + "] already registered.");
      }
      return addons.add(addon);
   }

   @Override
   public Map<Addon, ServiceRegistry> getServices()
   {
      Map<Addon, ServiceRegistry> services = new HashMap<Addon, ServiceRegistry>();
      for (Addon addon : addons)
      {
         services.put(addon, addon.getServiceRegistry());
      }
      return services;
   }

   @Override
   public Set<Addon> getRegisteredAddons()
   {
      return Collections.unmodifiableSet(addons);
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
}
