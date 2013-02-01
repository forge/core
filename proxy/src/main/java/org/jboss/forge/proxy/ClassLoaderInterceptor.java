package org.jboss.forge.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodHandler;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderInterceptor implements MethodHandler, ForgeProxy
{
   private ClassLoader loader;
   private Object delegate;

   public ClassLoaderInterceptor(ClassLoader loader, Object delegate)
   {
      this.loader = loader;
      this.delegate = delegate;
   }

   @Override
   public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args)
            throws Throwable
   {
      Callable<Object> task = new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            try
            {
               try
               {
                  if (thisMethod.getDeclaringClass().getName().equals(ForgeProxy.class.getName()))
                  {
                     return delegate;
                  }
               }
               catch (Exception e)
               {
               }

               return thisMethod.invoke(delegate, args);
            }
            catch (Throwable e)
            {
               throw new ContainerException(
                        "Failed during invocation of proxy method [" + delegate.getClass().getName() + "."
                                 + thisMethod.getName() + "()] in ClassLoader [" + loader + "]", e);
            }
         }
      };

      return ClassLoaders.executeIn(loader, task);
   }

   @Override
   public Object getDelegate()
   {
      return delegate;
   }

}