package org.jboss.forge.container.impl;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.RemoteInstance;
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
            if(bean == null)
            {
               System.out.println("wtf?");
            }
            context = manager.createCreationalContext(bean);
            return Enhancer.create((Class<?>) type,
                     new RemoteClassLoaderInterceptor(loader, manager.getReference(bean, type, context)));
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
   
   public class RemoteClassLoaderInterceptor implements MethodInterceptor
   {
      private ClassLoader loader;
      private Object delegate;

      public RemoteClassLoaderInterceptor(ClassLoader loader, Object delegate)
      {
         this.loader = loader;
         this.delegate = delegate;
      }

      @Override
      public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
               throws Throwable
      {
         Callable<Object> task = new Callable<Object>()
         {
            @Override
            public Object call() throws Exception
            {
               try
               {
                  return method.invoke(delegate, args);
               }
               catch (Throwable e)
               {
                  throw new ContainerException(
                           "Could not invoke proxy method [" + delegate.getClass().getName() + "."
                                    + method.getName() + "()] in ClassLoader ["
                                    + loader + "]", e);
               }
            }
         };

         return ClassLoaders.executeIn(loader, task);
      }

   }
}
