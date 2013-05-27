/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.services;

import java.util.concurrent.Callable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.util.Annotations;
import org.jboss.forge.furnace.util.ClassLoaders;
import org.jboss.forge.proxy.ClassLoaderInterceptor;
import org.jboss.forge.proxy.Proxies;

public class ExportedInstanceImpl<R> implements ExportedInstance<R>
{

   private ClassLoader loader;
   private BeanManager manager;
   private CreationalContext<R> context;

   private Bean<R> requestedBean;
   private Class<R> requestedType;
   private Class<? extends R> actualType;

   public ExportedInstanceImpl(ClassLoader loader, BeanManager manager, Bean<R> requestedBean, Class<R> requestedType,
            Class<? extends R> actualType)
   {
      this.loader = loader;
      this.manager = manager;
      this.requestedBean = requestedBean;
      this.requestedType = requestedType;
      this.actualType = actualType;
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
            context = manager.createCreationalContext(requestedBean);
            Object delegate = manager.getReference(requestedBean, actualType, context);
            return Proxies.enhance(loader, delegate, new ClassLoaderInterceptor(loader, delegate));
         }
      };

      try
      {
         return (R) ClassLoaders.executeIn(loader, task);
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to enhance instance of [" + actualType + "] with proxy for ClassLoader ["
                  + loader + "]", e);
      }
   }

   @SuppressWarnings("unchecked")
   public Object get(final InjectionPoint injectionPoint)
   {
      // FIXME remove the need for this method (which is currently still not working right for producer methods that
      // require an InjectionPoint
      Callable<Object> task = new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            Bean<R> bean = (Bean<R>) manager.resolve(manager.getBeans(actualType,
                     Annotations.getQualifiersFrom(actualType)));
            context = manager.createCreationalContext(bean);
            Object delegate = manager.getInjectableReference(injectionPoint, context);
            return Proxies.enhance(loader, delegate, new ClassLoaderInterceptor(loader, delegate));
         }
      };

      try
      {
         return ClassLoaders.executeIn(loader, task);
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to enhance instance of [" + actualType + "] with proxy for ClassLoader ["
                  + loader + "]");
      }
   }

   @Override
   public void release(R instance)
   {
      context.release();
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("ExportedInstanceImpl [");
      if (requestedType != null)
         builder.append("requestedType=").append(requestedType).append(", ");
      if (actualType != null)
         builder.append("actualType=").append(actualType).append(", ");
      if (loader != null)
         builder.append("loader=").append(loader);
      builder.append("]");
      return builder.toString();
   }

}
