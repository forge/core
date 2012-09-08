package org.jboss.forge.container.services;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public class RemoteInstanceImpl<R> implements RemoteInstance<R>
{
   private BeanManager manager;
   private Class<R> type;
   private CreationalContext<R> context;

   public RemoteInstanceImpl(BeanManager manager, Class<R> type)
   {
      this.manager = manager;
      this.type = type;
   }

   @Override
   @SuppressWarnings("unchecked")
   public R get()
   {
      Bean<R> bean = (Bean<R>) manager.resolve(manager.getBeans(type));
      context = manager.createCreationalContext(bean);
      return (R) manager.getReference(bean, type, context);
   }

   @Override
   public void release(R instance)
   {
      context.release();
   }
}
