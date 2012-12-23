package org.jboss.forge.se.init;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.util.ClassLoaders;

public class ClassLoaderAdapterCallback implements MethodInterceptor
{
   private final ClassLoader loader;
   private final Object delegate;

   public ClassLoaderAdapterCallback(ClassLoader loader, Object delegate)
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
               List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
               for (Class<?> type : method.getParameterTypes())
               {
                  parameterTypes.add(loader.loadClass(type.getName()));
               }

               Method delegateMethod = delegate.getClass().getMethod(method.getName(),
                        parameterTypes.toArray(new Class<?>[parameterTypes.size()]));

               return Enhancer.create(
                        method.getReturnType(),
                        new ClassLoaderAdapterCallback(loader, delegateMethod.invoke(delegate, args)));
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
