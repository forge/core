/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.resource;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.resource.events.ResourceEvent;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@Remote
@Singleton
public class ResourceFactory
{
   @Inject
   private BeanManager manager;

   @Inject
   private AddonRegistry registry;

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      synchronized (this)
      {
         for (RemoteInstance<ResourceGenerator> instance : getRegisteredResourceGenerators())
         {
            ResourceGenerator generator = instance.get();
            if (generator.handles(underlyingResource))
            {
               if (type.isAssignableFrom(generator.getResourceType(underlyingResource)))
               {
                  Resource<?> resource = generator.getResource(this, underlyingResource);
                  return (T) resource;
               }
            }
            instance.release(generator);
         }
      }
      return null;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E> Resource<E> create(E underlyingResource)
   {
      synchronized (this)
      {
         for (RemoteInstance<ResourceGenerator> instance : getRegisteredResourceGenerators())
         {
            ResourceGenerator generator = instance.get();
            if (generator.handles(underlyingResource))
            {
               Resource<?> resource = generator.getResource(this, underlyingResource);
               return (Resource<E>) resource;
            }
            instance.release(generator);
         }
      }
      return null;
   }

   @SuppressWarnings("rawtypes")
   private Iterable<RemoteInstance<ResourceGenerator>> getRegisteredResourceGenerators()
   {
      return registry.getRemoteServices(ResourceGenerator.class);
   }

   public ResourceFactory fireEvent(ResourceEvent event)
   {
      manager.fireEvent(event, new Annotation[] {});
      return this;
   }

}