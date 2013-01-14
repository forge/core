package org.jboss.forge.container.services;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

public class RemoteServiceProxyBeanProducer<T> implements Producer<T>
{
   private final Producer<T> wrapped;

   public RemoteServiceProxyBeanProducer(BeanManager manager, Producer<T> wrapped, Class<?> type)
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