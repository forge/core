/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderInterceptor implements ForgeProxy
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
               if (thisMethod.getDeclaringClass().getName().equals(ForgeProxy.class.getName()))
               {
                  return delegate;
               }
            }
            catch (Exception e)
            {
            }

            Object result;
            try
            {
               result = thisMethod.invoke(delegate, args);
            }
            catch (InvocationTargetException e)
            {
               if (e.getCause() instanceof Exception)
                  throw (Exception) e.getCause();
               throw e;
            }
            return result;
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