package org.jboss.forge.container.services;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

public class RemoteProducerWrapper<T extends Remote> implements Producer<T>
{
   private Producer<T> wrapped;
   private Class<T> type;

   public RemoteProducerWrapper(Producer<T> wrapped, Class<T> type)
   {
      this.wrapped = wrapped;
      this.type = type;
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