package org.jboss.forge.proxy;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.container.util.ClassLoaders;

class ProxyTypeInspector
{
   public static Class<?>[] getCompatibleClassHierarchy(ClassLoader loader, Class<?> origin)
   {
      Set<Class<?>> hierarchy = new LinkedHashSet<Class<?>>();

      Class<?> baseClass = origin;

      while (Modifier.isFinal(baseClass.getModifiers()))
      {
         baseClass = baseClass.getSuperclass();
      }

      if (ClassLoaders.containsClass(loader, baseClass.getName())
               && !Object.class.equals(baseClass)
               && (isInstantiable(baseClass) || baseClass.isInterface()))
      {
         hierarchy.add(ClassLoaders.loadClass(loader, baseClass));
      }

      baseClass = origin;
      while (baseClass != null)
      {
         for (Class<?> type : baseClass.getInterfaces())
         {
            if (ClassLoaders.containsClass(loader, type.getName()))
               hierarchy.add(ClassLoaders.loadClass(loader, type));
         }
         baseClass = baseClass.getSuperclass();
      }

      return hierarchy.toArray(new Class<?>[hierarchy.size()]);
   }

   private static boolean isInstantiable(Class<?> type)
   {
      try
      {
         type.getConstructor();
         return true;
      }
      catch (SecurityException e)
      {
      }
      catch (NoSuchMethodException e)
      {
      }
      return false;
   }

}