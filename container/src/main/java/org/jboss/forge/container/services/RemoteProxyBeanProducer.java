package org.jboss.forge.container.services;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

import org.jboss.forge.container.services.Remote;

public class RemoteProxyBeanProducer<T extends Remote> implements Producer<T>
{
   private Producer<T> wrapped;

   public RemoteProxyBeanProducer(Producer<T> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public T produce(CreationalContext<T> ctx)
   {
      return wrapped.produce(ctx);
   }

   @Override
   public void dispose(T instance)
   {
      wrapped.dispose(instance);
   }

   @Override
   public Set<InjectionPoint> getInjectionPoints()
   {
      return wrapped.getInjectionPoints();
   }

}