package org.jboss.forge.container;

import java.util.Map;
import java.util.Set;

import org.jboss.forge.container.services.ServiceRegistry;

public interface AddonRegistry
{

   Set<Addon> getRegisteredAddons();

   Map<ClassLoader, ServiceRegistry> getServices(Addon addon);

   Map<ClassLoader, ServiceRegistry> getServices();

}
