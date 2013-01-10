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

   public boolean isFailed();

   public boolean isStarted();

   public boolean isStarting();

   public boolean isStopped();

   public boolean isStopping();

   public boolean isWaiting();

}
