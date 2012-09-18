package org.jboss.forge.container.services;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.ClassLoaders.Task;

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
      Task task = new Task()
      {
         @Override
         public Object perform() throws Exception
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
