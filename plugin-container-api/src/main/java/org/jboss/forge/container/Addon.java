package org.jboss.forge.container;

import org.jboss.forge.container.services.ServiceRegistry;

public interface Addon
{
   public String getId();

   public ClassLoader getClassLoader();

   public ServiceRegistry getServiceRegistry();

   public Status getStatus();
}
