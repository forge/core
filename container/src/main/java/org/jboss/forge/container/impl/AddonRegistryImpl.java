package org.jboss.forge.container.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.RegisteredAddon;
import org.jboss.forge.container.RegisteredAddonFilter;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

public enum AddonRegistryImpl implements AddonRegistry
{
   INSTANCE;
   private Set<RegisteredAddon> addons = Sets.getConcurrentSet();

   @Override
   public RegisteredAddon getRegisteredAddon(AddonId id)
   {
      for (RegisteredAddon addon : addons)
      {
         if (addon.getId().equals(id))
            return addon;
      }
      return null;
   }

   public void register(RegisteredAddon addon)
   {
      if (!addons.contains(addon))
      {
         addons.add(addon);
      }
   }

   @Override
   public Map<RegisteredAddon, ServiceRegistry> getServiceRegistries()
   {
      Map<RegisteredAddon, ServiceRegistry> services = new HashMap<RegisteredAddon, ServiceRegistry>();
      for (RegisteredAddon addon : addons)
      {
         services.put(addon, addon.getServiceRegistry());
      }
      return services;
   }

   @Override
   public Set<RegisteredAddon> getRegisteredAddons()
   {
      return Collections.unmodifiableSet(addons);
   }

   @Override
   public Set<RegisteredAddon> getRegisteredAddons(RegisteredAddonFilter filter)
   {
      Set<RegisteredAddon> result = new HashSet<RegisteredAddon>();
      for (RegisteredAddon registeredAddon : addons)
      {
         if (filter.accept(registeredAddon))
            result.add(registeredAddon);
      }
      return result;
   }

   public void removeServices(ClassLoader classLoader) throws IllegalArgumentException
   {
      for (RegisteredAddon addon : addons)
      {
         if (addon.getClassLoader().equals(classLoader))
         {
            addons.remove(addon);
         }
      }
   }

   public void remove(RegisteredAddon addon)
   {
      addons.remove(addon);
   }

   @Override
   public boolean isRegistered(AddonId id)
   {
      return getRegisteredAddon(id) != null;
   }

   @Override
   public String toString()
   {
      return addons.toString();
   }

   @Override
   public <T> Set<RemoteInstance<T>> getRemoteServices(Class<T> type)
   {
      // TODO This needs to block addon installation/removal;
      Set<RemoteInstance<T>> result = new HashSet<RemoteInstance<T>>();
      for (RegisteredAddon addon : addons)
      {
         ServiceRegistry serviceRegistry = addon.getServiceRegistry();
         result.addAll(serviceRegistry.getRemoteInstances(type));
      }
      return result;
   }
}
