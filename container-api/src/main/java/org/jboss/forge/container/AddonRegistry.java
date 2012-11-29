package org.jboss.forge.container;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.services.ServiceRegistry;

public interface AddonRegistry
{
   RegisteredAddon getRegisteredAddon(AddonId entry);

   Set<RegisteredAddon> getRegisteredAddons();

   boolean isRegistered(AddonId id);

   boolean isWaiting(RegisteredAddon addon);

   Map<RegisteredAddon, ServiceRegistry> getServices();

   Map<RegisteredAddon, Set<RegisteredAddon>> getWaitlistedAddons();

}
