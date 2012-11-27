package org.jboss.forge.container.services;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.util.ClassLoaders;

public class RemoteClassLoaderCallback implements MethodInterceptor
{
   private ClassLoader loader;
   private Object delegate;

   public RemoteClassLoaderCallback(ClassLoader loader, Object delegate)
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
