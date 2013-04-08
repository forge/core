/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.shell.events.CommandExecuted;
import org.jboss.forge.shell.events.CommandVetoed;
import org.jboss.forge.shell.events.PreCommandExecution;

/**
 * This class provides lifecycle management for {@link CommandScoped} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class CommandScopedContext implements Context
{
   private final static String COMPONENT_MAP_NAME = CommandScopedContext.class.getName() + ".componentInstanceMap";
   private final static String CREATIONAL_MAP_NAME = CommandScopedContext.class.getName() + ".creationalInstanceMap";
   private static final Stack<Map<Object, Object>> contextStack = new Stack<Map<Object, Object>>();

   private void assertActive()
   {
      if (!isActive())
      {
         throw new ContextNotActiveException(
                  "Context with scope annotation @CommandScoped is not active since no command is in execution.");
      }
   }

   public Map<Object, Object> getCurrentContext()
   {
      return contextStack.peek();
   }

   public void create(@Observes final PreCommandExecution execution)
   {
      contextStack.push(execution.getContext());
   }

   public void destroy(@Observes final CommandExecuted event)
   {
      destroyCurrentContext();
      contextStack.pop();
   }

   public void destroy(@Observes final CommandVetoed event)
   {
      destroyCurrentContext();
      contextStack.pop();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
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
      getCurrentContext().clear();
   }

   /*
    * Context Methods
    */

   @Override
   public boolean isActive()
   {
      return !contextStack.isEmpty();
   }

   @Override
   public Class<? extends Annotation> getScope()
   {
      return CommandScoped.class;
   }

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

   /*
    * Helpers for manipulating the Component/Context maps.
    */
   @SuppressWarnings("unchecked")
   private Map<Contextual<?>, Object> getComponentInstanceMap()
   {
      ConcurrentHashMap<Contextual<?>, Object> map = (ConcurrentHashMap<Contextual<?>, Object>) getCurrentContext()
               .get(COMPONENT_MAP_NAME);

      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, Object>();
         getCurrentContext().put(COMPONENT_MAP_NAME, map);
      }

      return map;
   }

   @SuppressWarnings("unchecked")
   private Map<Contextual<?>, CreationalContext<?>> getCreationalContextMap()
   {
      Map<Contextual<?>, CreationalContext<?>> map = (ConcurrentHashMap<Contextual<?>, CreationalContext<?>>) getCurrentContext()
               .get(CREATIONAL_MAP_NAME);

      if (map == null)
      {
         map = new ConcurrentHashMap<Contextual<?>, CreationalContext<?>>();
         getCurrentContext().put(CREATIONAL_MAP_NAME, map);
      }

      return map;
   }
}