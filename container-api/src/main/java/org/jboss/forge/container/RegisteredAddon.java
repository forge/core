package org.jboss.forge.container;

import org.jboss.forge.container.services.ServiceRegistry;

public interface RegisteredAddon
{
   public AddonId getId();

   public ClassLoader getClassLoader();

   public ServiceRegistry getServiceRegistry();

   public Status getStatus();
}
