package org.jboss.forge.container;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;

public interface AddonRegistry
{
   Addon getRegisteredAddon(AddonId entry);

   Set<Addon> getRegisteredAddons();

   Set<Addon> getRegisteredAddons(AddonFilter filter);

   boolean isRegistered(AddonId id);

   Map<Addon, ServiceRegistry> getServiceRegistries();

   <T> Set<RemoteInstance<T>> getRemoteServices(Class<T> type);
}
