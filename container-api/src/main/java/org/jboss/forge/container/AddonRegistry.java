package org.jboss.forge.container;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.services.ServiceRegistry;

public interface AddonRegistry
{
   RegisteredAddon getRegisteredAddon(AddonId entry);

   Set<RegisteredAddon> getRegisteredAddons();

   Set<RegisteredAddon> getRegisteredAddons(RegisteredAddonFilter filter);

   boolean isRegistered(AddonId id);

   Map<RegisteredAddon, ServiceRegistry> getServices();
}
