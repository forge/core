package org.jboss.forge.container.services;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

import net.sf.cglib.proxy.Enhancer;

public class RemoteBeanProducer<T extends Remote> implements Producer<T>
{
   private Producer<T> wrapped;
   private Class<?> type;
   private BeanManager manager;

   public RemoteBeanProducer(BeanManager manager, Producer<T> wrapped, Class<?> type)
   {
      this.manager = manager;
      this.wrapped = wrapped;
      this.type = type;
   }

   @Produces
   @Service
   public Remote produceGenericService()
   {
      return null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public T produce(CreationalContext<T> ctx)
   {
      return (T) Enhancer.create(type, new RemoteServiceCallback(manager, ctx, type));
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