/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
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