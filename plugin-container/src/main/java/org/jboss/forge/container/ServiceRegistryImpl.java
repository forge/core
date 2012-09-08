package org.jboss.forge.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;

@Singleton
public class ServiceRegistryImpl implements ServiceRegistry
{
   private Map<Class<?>, RemoteInstance<?>> services = new ConcurrentHashMap<Class<?>, RemoteInstance<?>>();

   @Override
   public <T> void addService(Class<T> clazz, RemoteInstance<T> service)
   {
      services.put(clazz, service);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> RemoteInstance<T> getRemoteInstance(Class<T> clazz)
   {
      return (RemoteInstance<T>) services.get(clazz);
   }

   @Override
   public Map<Class<?>, RemoteInstance<?>> getServices()
   {
      return services;
   }

}
