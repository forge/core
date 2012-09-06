package org.jboss.forge.container.services;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import net.sf.cglib.proxy.LazyLoader;

public class RemoteServiceCallback implements LazyLoader
{
   private CreationalContext<?> creationalContext;
   private Class<?> serviceType;
   private BeanManager manager;

   public RemoteServiceCallback(BeanManager manager, CreationalContext<?> creationalContext, Class<?> serviceType)
   {
      this.manager = manager;
      this.creationalContext = creationalContext;
      this.serviceType = serviceType;
   }

   @Override
   public Object loadObject() throws Exception
   {
      // TODO this instance needs to come from a remote Weld container
      return serviceType.newInstance();
   }
}
