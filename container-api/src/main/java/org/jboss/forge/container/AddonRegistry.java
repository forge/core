package org.jboss.forge.container;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;

public interface AddonRegistry
{
   Addon getRegisteredAddon(AddonId id);

   Set<Addon> getRegisteredAddons();

   Set<Addon> getRegisteredAddons(AddonFilter filter);

   boolean isRegistered(AddonId id);

   Map<Addon, ServiceRegistry> getServiceRegistries();

   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   Future<?> start(Addon addon);

   void stop(Addon addon);
}
