package org.jboss.forge.container.services;

import java.util.concurrent.Callable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import net.sf.cglib.proxy.Enhancer;

import org.jboss.forge.container.util.ClassLoaders;

public class RemoteInstanceImpl<R> implements RemoteInstance<R>
{
   private ClassLoader loader;
   private BeanManager manager;
   private Class<R> type;
   private CreationalContext<R> context;

   public RemoteInstanceImpl(ClassLoader loader, BeanManager manager, Class<R> type)
   {
      this.loader = loader;
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
            return Enhancer.create((Class<?>) type,
                     new RemoteClassLoaderCallback(loader, manager.getReference(bean, type, context)));
         }
      };

      return (R) ClassLoaders.executeIn(loader, task);
   }

   @Override
   public void release(R instance)
   {
      context.release();
   }

   @Override
   public String toString()
   {
      return "RemoteInstanceImpl [type=" + type + ", classLoader=" + type.getClassLoader() + "]";
   }
}
