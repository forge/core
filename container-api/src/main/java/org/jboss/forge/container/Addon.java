package org.jboss.forge.container;

import java.util.Set;

import org.jboss.forge.container.services.ServiceRegistry;

public interface Addon
{

   public AddonId getId();

   public ClassLoader getClassLoader();

   public ServiceRegistry getServiceRegistry();

   public Status getStatus();

   public Set<AddonDependency> getDependencies();

}
