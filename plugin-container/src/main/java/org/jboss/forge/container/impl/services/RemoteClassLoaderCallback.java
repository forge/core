package org.jboss.forge.container.impl.services;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.forge.container.impl.exception.ContainerException;
import org.jboss.forge.container.impl.util.ClassLoaders;

public class RemoteClassLoaderCallback implements MethodInterceptor
{
   private Object delegate;

   public RemoteClassLoaderCallback(Object delegate)
   {
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
               return method.invoke(obj, args);
            }
            catch (Throwable e)
            {
               throw new ContainerException(
                        "Could not invoke proxy method [" + method.getDeclaringClass().getName() + "."
                                 + method.getName() + "()] in ClassLoader ["
                                 + Thread.currentThread().getContextClassLoader() + "]", e);
            }
         }
      };

      return ClassLoaders.executeIn(delegate.getClass().getClassLoader(), task);
   }

}
