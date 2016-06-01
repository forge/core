/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.extension;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.furnace.addons.AddonId;

public class AnnotatedCommandExtension implements Extension
{
   private static final Map<AddonId, Set<Method>> annotationMethods = new ConcurrentHashMap<>();

   public <T> void observeAnnotationMethods(@WithAnnotations(Command.class) @Observes ProcessAnnotatedType<T> bean,
            final BeanManager beanManager)
   {
      AnnotatedType<T> annotatedType = bean.getAnnotatedType();
      AddonId addonId = AddonId.fromCoordinates(Thread.currentThread().getName());
      for (AnnotatedMethod<? super T> annotatedMethod : annotatedType.getMethods())
      {
         if (annotatedMethod.isAnnotationPresent(Command.class))
         {
            Set<Method> set = annotationMethods.get(addonId);
            if (set == null)
            {
               set = new HashSet<>();
               annotationMethods.put(addonId, set);
            }
            set.add(annotatedMethod.getJavaMember());
         }
      }
   }

   public Set<Method> getAnnotatedCommandMethods()
   {
      Set<Method> set = new HashSet<>();
      for (Set<Method> methodSet : annotationMethods.values())
      {
         set.addAll(methodSet);
      }
      return set;
   }

   public void addonDestroyed(AddonId addonId)
   {
      annotationMethods.remove(addonId);
   }
}
