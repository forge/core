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

      while (baseClass != null && Modifier.isFinal(baseClass.getModifiers()))
      {
         baseClass = baseClass.getSuperclass();
      }

      while (baseClass != null
               && !baseClass.isInterface()
               && baseClass.getSuperclass() != null
               && !baseClass.getSuperclass().equals(Object.class)
               && !Proxies.isInstantiable(baseClass))
      {
         baseClass = baseClass.getSuperclass();
      }

      if (baseClass != null && ClassLoaders.containsClass(loader, baseClass.getName())
               && !Object.class.equals(baseClass)
               && (Proxies.isInstantiable(baseClass) || baseClass.isInterface()))
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

}