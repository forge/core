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
   public static <T> T enhance(final ClassLoader loader, Object instance, ForgeProxy handler)
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

      ProxyFactory f = new ProxyFactory()
      {
         @Override
         protected ClassLoader getClassLoader()
         {
            return loader;
         }
      };

      f.setUseCache(true);

      Class<?>[] hierarchy = null;
      Class<?> unwrappedInstanceType = Proxies.unwrapProxyTypes(instance.getClass(), loader);
      hierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(loader, unwrappedInstanceType);
      if (hierarchy == null || hierarchy.length == 0)
         throw new IllegalArgumentException("Must specify at least one non-final type to enhance for Object: "
                  + instance + " of type " + instance.getClass());

      Class<?> first = hierarchy[0];
      if (!first.isInterface())
      {
         f.setSuperclass(Proxies.unwrapProxyTypes(first, loader));
         hierarchy = Arrays.shiftLeft(hierarchy, new Class<?>[hierarchy.length - 1]);
      }

      int index = Arrays.indexOf(hierarchy, ProxyObject.class);
      if (index >= 0)
      {
         hierarchy = Arrays.removeElementAtIndex(hierarchy, index);
      }

      if (!Proxies.isProxyType(first) && !Arrays.contains(hierarchy, ForgeProxy.class))
         hierarchy = Arrays.append(hierarchy, ForgeProxy.class);

      if (hierarchy.length > 0)
         f.setInterfaces(hierarchy);

      f.setFilter(filter);

      Class<?> c = f.createClass();

      try
      {
         enhancedResult = c.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new IllegalStateException(
                  "Could not instantiate proxy for object [" + instance + "] of type [" + unwrappedInstanceType
                           + "]. For optimal proxy compatability, ensure " +
                           "that this type is an interface, or a class with a default constructor.", e);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException(e);
      }

      ((ProxyObject) enhancedResult).setHandler(handler);

      return (T) enhancedResult;
   }

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
      Class<?> c;
      try
      {
         c = f.createClass();
      }
      catch (RuntimeException e)
      {
         throw e;
      }

      try
      {
         enhancedResult = c.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new IllegalStateException(
                  "Could not instantiate proxy for type [" + type
                           + "]. For optimal proxy compatability, ensure " +
                           "that this type is an interface, or a class with a default constructor.", e);
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

   @SuppressWarnings("unchecked")
   public static <T> T unwrapOnce(Object object)
   {
      T result = (T) object;

      if (object != null)
      {
         if (isForgeProxy(result))
         {
            try
            {
               Method method = result.getClass().getMethod("getDelegate");
               method.setAccessible(true);
               result = (T) method.invoke(result);
            }
            catch (Exception e)
            {
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

         String typeName = unwrapProxyClassName(result);
         for (ClassLoader loader : loaders)
         {
            try
            {
               result = loader.loadClass(typeName);
               break;
            }
            catch (ClassNotFoundException e)
            {
            }
         }
      }
      return result;
   }

   /**
    * Unwraps the proxy type if javassist or CGLib is used
    * 
    * @param type the class type
    * @return the unproxied class name
    */
   public static String unwrapProxyClassName(Class<?> type)
   {
      String typeName;
      if (type.getName().contains("$$EnhancerByCGLIB$$"))
      {
         typeName = type.getName().replaceAll("^(.*)\\$\\$EnhancerByCGLIB\\$\\$.*", "$1");
      }
      else if (type.getName().contains("_javassist_"))
      {
         typeName = type.getName().replaceAll("^(.*)_\\$\\$_javassist_.*", "$1");
      }
      else
      {
         typeName = type.getName();
      }
      return typeName;
   }

   /**
    * This method tests if two proxied objects are equivalent.
    * 
    * It does so by comparing the class names and the hashCode, since they may be loaded from different classloaders.
    * 
    */
   public static boolean areEquivalent(Object proxiedObj, Object anotherProxiedObj)
   {
      if (proxiedObj == null && anotherProxiedObj == null)
      {
         return true;
      }
      else if (proxiedObj == null || anotherProxiedObj == null)
      {
         return false;
      }
      else
      {
         Object unproxiedObj = unwrap(proxiedObj);
         Object anotherUnproxiedObj = unwrap(anotherProxiedObj);

         boolean sameClassName = unwrapProxyClassName(unproxiedObj.getClass()).equals(
                  unwrapProxyClassName(anotherUnproxiedObj.getClass()));
         if (sameClassName)
         {
            if (unproxiedObj.getClass().isEnum())
            {
               // Enum hashCode is different if loaded from different classloaders and cannot be overriden.
               Enum<?> enumLeft = Enum.class.cast(unproxiedObj);
               Enum<?> enumRight = Enum.class.cast(anotherUnproxiedObj);
               return (enumLeft.name().equals(enumRight.name())) && (enumLeft.ordinal() == enumRight.ordinal());
            }
            else
            {
               return (unproxiedObj.hashCode() == anotherUnproxiedObj.hashCode());
            }
         }
         else
         {
            return false;
         }
      }
   }

   /**
    * Checks if a proxied object is an instance of the specified {@link Class}
    */
   public static boolean isInstance(Class<?> type, Object proxiedObject)
   {
      return type.isInstance(unwrap(proxiedObject));
   }

   /**
    * Determine whether or not a given {@link Class} type is instantiable.
    */
   public static boolean isInstantiable(Class<?> type)
   {
      if (type != null)
      {
         try
         {
            if (type.isInterface())
               return true;
            type.getConstructor();
            return true;
         }
         catch (SecurityException e)
         {
            return false;
         }
         catch (NoSuchMethodException e)
         {
            return false;
         }
      }
      return false;
   }

   /**
    * Determine if the given {@link Class} type does not require {@link ClassLoader} proxying.
    */
   public static boolean isPassthroughType(Class<?> type)
   {
      boolean result = type.getName().startsWith("[L")
               || type.getName().matches("^(java\\.lang).*")
               || type.getName().matches("^(java\\.io).*")
               || type.getName().matches("^(java\\.net).*")
               || type.isPrimitive();

      result = result && !(Iterable.class.getName().equals(type.getName()));

      return result;
   }

   public static boolean isLanguageType(Class<?> type)
   {
      boolean result = type.getName().startsWith("[L")
               || type.getName().matches("^(java\\.).*")
               || type.isPrimitive();

      return result;
   }
}
