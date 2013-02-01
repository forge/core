/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Proxies
{
   /**
    * Create a proxy for the given {@link Class} type.
    */
   @SuppressWarnings("unchecked")
   public static <T> T enhance(Class<T> type, ForgeProxy handler)
   {
      MethodFilter filter = new MethodFilter()
      {
         @Override
         public boolean isHandled(Method method)
         {
            String name = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (!method.getDeclaringClass().getName().contains("java.lang")
                     || ("clone".equals(name) && parameterTypes.length == 0)
                     || ("equals".equals(name) && parameterTypes.length == 1)
                     || ("hashCode".equals(name) && parameterTypes.length == 0)
                     || ("toString".equals(name) && parameterTypes.length == 0))
               return true;
            return false;
         }
      };

      Object enhancedResult = null;

      ProxyFactory f = new ProxyFactory();

      f.setUseCache(true);

      if (type.isInterface() && !ForgeProxy.class.isAssignableFrom(type))
         f.setInterfaces(new Class<?>[] { type, ForgeProxy.class });
      else if (type.isInterface())
         f.setInterfaces(new Class<?>[] { type });
      else
      {
         if (Proxies.isProxyType(type))
            f.setSuperclass(unwrapProxyTypes(type));
         else
         {
            f.setSuperclass(type);
            f.setInterfaces(new Class<?>[] { ForgeProxy.class });
         }
      }

      f.setFilter(filter);
      Class<?> c = f.createClass();

      try
      {
         enhancedResult = c.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new IllegalStateException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException(e);
      }

      ((ProxyObject) enhancedResult).setHandler(handler);

      return (T) enhancedResult;
   }

   public static boolean isProxyType(Class<?> type)
   {
      if (type.getName().contains("$$EnhancerByCGLIB$$") || type.getName().contains("_javassist_"))
      {
         return true;
      }
      return false;
   }

   /**
    * Returns the delegate object, if the given object was created via {@link Proxies}, otherwise it returns the given
    * object, unchanged.
    */
   @SuppressWarnings("unchecked")
   public static <T> T unwrap(Object object)
   {
      T result = (T) object;

      if (object != null)
      {
         while (isForgeProxy(result))
         {
            try
            {
               Method method = result.getClass().getMethod("getDelegate");
               method.setAccessible(true);
               result = (T) method.invoke(result);
            }
            catch (Exception e)
            {
               break;
            }
         }

         if (result == null)
            result = (T) object;
      }
      return result;
   }

   /**
    * Returns true if the given object was created via {@link Proxies}.
    */
   public static boolean isForgeProxy(Object object)
   {
      if (object != null)
      {
         Class<?>[] interfaces = object.getClass().getInterfaces();
         if (interfaces != null)
         {
            for (Class<?> iface : interfaces)
            {
               if (iface.getName().equals(ForgeProxy.class.getName()))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }

   public static Class<?> unwrapProxyTypes(Class<?> type, ClassLoader... loaders)
   {
      Class<?> result = type;

      if (isProxyType(result))
      {
         Class<?> superclass = result.getSuperclass();
         while (superclass != null && !superclass.getName().equals(Object.class.getName()) && isProxyType(superclass))
         {
            superclass = superclass.getSuperclass();
         }

         if (superclass != null && !superclass.getName().equals(Object.class.getName()))
            return superclass;

         for (ClassLoader loader : loaders)
         {
            try
            {
               if (result.getName().contains("$$EnhancerByCGLIB$$"))
               {
                  String typeName = result.getName().replaceAll("^(.*)\\$\\$EnhancerByCGLIB\\$\\$.*", "$1");
                  result = loader.loadClass(typeName);
                  break;
               }
               else if (result.getName().contains("_javassist_"))
               {
                  String typeName = result.getName().replaceAll("^(.*)_javassist_.*", "$1");
                  result = loader.loadClass(typeName);
                  break;
               }
            }
            catch (ClassNotFoundException e)
            {
            }
         }
      }
      return result;
   }

}
