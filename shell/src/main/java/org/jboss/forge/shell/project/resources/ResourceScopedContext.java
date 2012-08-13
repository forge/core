/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project.resources;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.project.Project;
import org.jboss.forge.shell.events.ResourceChanged;
import org.jboss.forge.shell.project.ResourceScoped;
import org.jboss.forge.shell.util.BeanManagerUtils;

/**
 * This class provides lifecycle management for the {@link Project} scope
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ResourceScopedContext implements Context
{
   private final static String COMPONENT_MAP_NAME = ResourceScopedContext.class.getName() + ".componentInstanceMap";
   private final static String CREATIONAL_MAP_NAME = ResourceScopedContext.class.getName() + ".creationalInstanceMap";

   private final BeanManager manager;
   private final Map<String, Object> contextMap = new ConcurrentHashMap<String, Object>();

   @Inject
   public ResourceScopedContext(final BeanManager manager)
   {
      this.manager = manager;
   }

   private void assertActive()
   {
      if (!isActive())
      {
         throw new ContextNotActiveException(
                  "Context with scope annotation @ProjectScoped is not active with respect to the current directory.");
      }
   }

   private ResourceScopedContext getCurrentContext()
   {
      // ensure that we get the currently active context when this method is invoked
      ResourceScopedContext scopedContext = BeanManagerUtils
               .getContextualInstance(manager, ResourceScopedContext.class);
      return scopedContext;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void destroy(@Observes final ResourceChanged event)
   {
      if (getCurrentContext() != null)
      {
         Map<Contextual<?>, Object> componentInstanceMap = getComponentInstanceMap();
         Map<Contextual<?>, CreationalContext<?>> creationalContextMap = getCreationalContextMap();

         if ((componentInstanceMap != null) && (creationalContextMap != null))
         {
            for (Entry<Contextual<?>, Object> componentEntry : componentInstanceMap.entrySet())
            {
               Contextual contextual = componentEntry.getKey();
               Object instance = componentEntry.getValue();
               CreationalContext creational = creationalContextMap.get(contextual);

               contextual.destroy(instance, creational);
            }
         }

         getCurrentContext().contextMap.clear();
      }
   }

   /*
    * Context Methods
    */
   @Override
   @SuppressWarnings("unchecked")
   public <T> T get(final Contextual<T> component)
   {
      assertActive();
      return (T) getComponentInstanceMap().get(component);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T get(final Contextual<T> component, final CreationalContext<T> creationalContext)
   {
      assertActive();

      T instance = get(component);

      if (instance == null)
      {
         Map<Contextual<?>, CreationalContext<?>> creationalContextMap = getCreationalContextMap();
         Map<Contextual<?>, Object> componentInstanceMap = getComponentInstanceMap();

         synchronized (componentInstanceMap)
         {
            instance = (T) componentInstanceMap.get(component);
            if (instance == null)
            {
               instance = component.create(creationalContext);

               if (instance != null)
               {
                  componentInstanceMap.put(component, instance);
                  creationalContextMap.put(component, creationalContext);
               }
            }
         }
      }

      return instance;
   }

   @Override
   public boolean isActive()
   {
      return getCurrentContext() != null;
   }

   @Override
   public Class<? extends Annotation> getScope()
   {
      return ResourceScoped.class;
   }

   /*
    * Helpers for manipulating the Component/Context maps.
    */
   @SuppressWarnings("unchecked")
   private Map<Contextual<?>, Object> getComponentInstanceMap()
   {
      ConcurrentHashMap<Contextual<?>, Object> map = (ConcurrentHashMap<Contextual<?>, Object>) getCurrentContext()
               .contextMap.get(COMPONENT_MAP_NAME);

      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, Object>();
         getCurrentContext().contextMap.put(COMPONENT_MAP_NAME, map);
      }

      return map;
   }

   @SuppressWarnings("unchecked")
   private Map<Contextual<?>, CreationalContext<?>> getCreationalContextMap()
   {
      Map<Contextual<?>, CreationalContext<?>> map = (ConcurrentHashMap<Contextual<?>, CreationalContext<?>>) getCurrentContext()
               .contextMap.get(CREATIONAL_MAP_NAME);

      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, CreationalContext<?>>();
         getCurrentContext().contextMap.put(CREATIONAL_MAP_NAME, map);
      }

      return map;
   }
}