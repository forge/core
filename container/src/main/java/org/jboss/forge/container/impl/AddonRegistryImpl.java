package org.jboss.forge.container.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonFilter;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;

public enum AddonRegistryImpl implements AddonRegistry
{
   INSTANCE;
   private Set<Addon> addons = Sets.getConcurrentSet();

   @Override
   public Addon getRegisteredAddon(AddonId id)
   {
      for (Addon addon : addons)
      {
         if (addon.getId().equals(id))
            return addon;
      }
      return null;
   }

   public void register(Addon addon)
   {
      if (!addons.contains(addon))
      {
         addons.add(addon);
      }
   }

   @Override
   public Map<Addon, ServiceRegistry> getServiceRegistries()
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

   @Override
   public Set<Addon> getRegisteredAddons(AddonFilter filter)
   {
      Set<Addon> result = new HashSet<Addon>();
      for (Addon registeredAddon : addons)
      {
         if (filter.accept(registeredAddon))
            result.add(registeredAddon);
      }
      return result;
   }

   public void removeServices(ClassLoader classLoader) throws IllegalArgumentException
   {
      for (Addon addon : addons)
      {
         if (addon.getClassLoader().equals(classLoader))
         {
            addons.remove(addon);
         }
      }
   }

   public void remove(Addon addon)
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
   public <T> Set<RemoteInstance<T>> getRemoteInstances(Class<T> type)
   {
      // TODO This needs to block addon installation/removal;
      Set<RemoteInstance<T>> result = new HashSet<RemoteInstance<T>>();
      for (Addon addon : addons)
      {
         if (Status.STARTED.equals(addon.getStatus()))
         {
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            result.addAll((Collection<? extends RemoteInstance<T>>) serviceRegistry.getRemoteInstances(type));
         }
      }
      return result;
   }

   @Override
   public <T> Set<RemoteInstance<T>> getRemoteInstances(String typeName)
   {
      // TODO This needs to block addon installation/removal;
      Set<RemoteInstance<T>> result = new HashSet<RemoteInstance<T>>();
      for (Addon addon : addons)
      {
         if (Status.STARTED.equals(addon.getStatus()))
         {
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            Set<RemoteInstance<T>> remoteInstances = serviceRegistry.getRemoteInstances(typeName);
            result.addAll(remoteInstances);
         }
      }
      return result;
   }
}
