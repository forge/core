/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.jboss.forge.addon.ui.cdi.CommandScoped;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
public class CommandScopedContext implements Context, UIContextListener
{
   private final static String COMPONENT_MAP_NAME = CommandScopedContext.class.getName() + ".componentInstanceMap";
   private final static String CREATIONAL_MAP_NAME = CommandScopedContext.class.getName() + ".creationalInstanceMap";
   private static final LinkedList<UIContext> CONTEXT_STACK = new LinkedList<UIContext>();

   @Override
   public Class<? extends Annotation> getScope()
   {
      return CommandScoped.class;
   }

   @Override
   public <T> T get(final Contextual<T> component)
   {
      assertActive();
      return (T) getComponentInstanceMap().get(component);
   }

   @Override
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

   private void assertActive()
   {
      if (!isActive())
      {
         throw new ContextNotActiveException(
                  "Context with scope annotation @CommandScoped is not active since no UICommand is in execution.");
      }
   }

   static UIContext getCurrentContext()
   {
      return CONTEXT_STACK.peek();
   }

   @SuppressWarnings({ "rawtypes" })
   private void destroyCurrentContext()
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
   }

   @Override
   public boolean isActive()
   {
      return !CONTEXT_STACK.isEmpty();
   }

   @Override
   public void contextInitialized(UIContext context)
   {
      CONTEXT_STACK.push(context);
   }

   @Override
   public void contextDestroyed(UIContext context)
   {
      destroyCurrentContext();
      CONTEXT_STACK.pop();
   }

   /*
    * Helpers for manipulating the Component/Context maps.
    */
   private Map<Contextual<?>, Object> getComponentInstanceMap()
   {
      ConcurrentHashMap<Contextual<?>, Object> map = (ConcurrentHashMap<Contextual<?>, Object>) getCurrentContext()
               .getAttributeMap().get(COMPONENT_MAP_NAME);
      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, Object>();
         getCurrentContext().getAttributeMap().put(COMPONENT_MAP_NAME, map);
      }
      return map;
   }

   private Map<Contextual<?>, CreationalContext<?>> getCreationalContextMap()
   {
      Map<Contextual<?>, CreationalContext<?>> map = (ConcurrentHashMap<Contextual<?>, CreationalContext<?>>) getCurrentContext()
               .getAttributeMap().get(CREATIONAL_MAP_NAME);
      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, CreationalContext<?>>();
         getCurrentContext().getAttributeMap().put(CREATIONAL_MAP_NAME, map);
      }
      return map;
   }
}