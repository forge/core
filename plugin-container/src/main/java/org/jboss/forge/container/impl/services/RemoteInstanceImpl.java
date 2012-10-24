package org.jboss.forge.container.impl.services;

import java.util.concurrent.Callable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.impl.util.ClassLoaders;
import org.jboss.forge.container.services.RemoteInstance;

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
      Callable<Object> task = new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            Bean<R> bean = (Bean<R>) manager.resolve(manager.getBeans(type));
            context = manager.createCreationalContext(bean);
            return manager.getReference(bean, type, context);
         }
      };

      return (R) ClassLoaders.executeIn(type.getClassLoader(), task);
   }

   @Override
   public void release(R instance)
   {
      context.release();
   }
}
