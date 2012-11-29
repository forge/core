package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.RegisteredAddon;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

@Typed()
public class AddonRegistryImpl implements AddonRegistry
{
   private Map<RegisteredAddon, Set<RegisteredAddon>> addonMap = new ConcurrentHashMap<RegisteredAddon, Set<RegisteredAddon>>();

   /**
    * Global RegisteredAddon registry.
    */
   public static AddonRegistryImpl registry = new AddonRegistryImpl();

   @Override
   public RegisteredAddon getRegisteredAddon(AddonId id)
   {
      for (RegisteredAddon addon : addonMap.keySet())
      {
         if (addon.getId().equals(id))
            return addon;
      }
      return null;
   }

   public void register(RegisteredAddon addon)
   {
      if (!addonMap.containsKey(addon))
      {
         addonMap.put(addon, Sets.getConcurrentSet(RegisteredAddon.class));
      }
   }

   @Override
   public Map<RegisteredAddon, ServiceRegistry> getServices()
   {
      Map<RegisteredAddon, ServiceRegistry> services = new HashMap<RegisteredAddon, ServiceRegistry>();
      for (RegisteredAddon addon : addonMap.keySet())
      {
         services.put(addon, addon.getServiceRegistry());
      }
      return services;
   }

   @Override
   public Set<RegisteredAddon> getRegisteredAddons()
   {
      return Collections.unmodifiableSet(addonMap.keySet());
   }

   @Override
   public Map<RegisteredAddon, Set<RegisteredAddon>> getWaitlistedAddons()
   {
      Map<RegisteredAddon, Set<RegisteredAddon>> result = new HashMap<RegisteredAddon, Set<RegisteredAddon>>();
      for (Entry<RegisteredAddon, Set<RegisteredAddon>> entry : addonMap.entrySet())
      {
         if (!entry.getValue().isEmpty())
            result.put(entry.getKey(), entry.getValue());
      }
      return Collections.unmodifiableMap(result);
   }

   public Map<RegisteredAddon, Set<RegisteredAddon>> getMutableWaitlist()
   {
      return addonMap;
   }

   public void removeServices(ClassLoader classLoader) throws IllegalArgumentException
   {
      for (RegisteredAddon addon : addonMap.keySet())
      {
         if (addon.getClassLoader().equals(classLoader))
         {
            addonMap.remove(addon);
         }
      }
   }

   public void remove(RegisteredAddon addon)
   {
      addonMap.remove(addon);
   }

   @Override
   public String toString()
   {
      return addonMap.keySet().toString();
   }

   @Override
   public boolean isRegistered(AddonId id)
   {
      return getRegisteredAddon(id) != null;
   }

   @Override
   public boolean isWaiting(RegisteredAddon addon)
   {
      return isRegistered(addon.getId()) && !addonMap.get(addon).isEmpty();
   }
}
