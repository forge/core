/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.util.TreeMap;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionManager;
import org.jboss.forge.addon.resource.util.RelatedClassComparator;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * @author Mike Brock <cbrock@redhat.com>
 */
@Singleton
public class ResourceFactoryImpl implements ResourceFactory, ResourceTransactionManager
{
   @Inject
   private BeanManager manager;

   @Inject
   private AddonRegistry registry;

   private ResourceTransactionImpl transaction;

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      T result = null;
      synchronized (this)
      {
         TreeMap<Class<?>, Resource<?>> generated = new TreeMap<Class<?>, Resource<?>>(new RelatedClassComparator());
         for (ExportedInstance<ResourceGenerator> instance : getRegisteredResourceGenerators())
         {
            ResourceGenerator generator = instance.get();
            if (generator.handles(type, underlyingResource))
            {
               Class resourceType = generator.getResourceType(type, underlyingResource);
               if (type.isAssignableFrom(resourceType))
               {
                  generated.put(resourceType, generator.getResource(this, type, underlyingResource));
               }
            }
            instance.release(generator);
         }
         if (generated.size() > 0)
         {
            result = (T) generated.lastEntry().getValue();
         }
      }
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <E> Resource<E> create(E underlyingResource)
   {
      return create(Resource.class, underlyingResource);
   }

   @SuppressWarnings("rawtypes")
   private Iterable<ExportedInstance<ResourceGenerator>> getRegisteredResourceGenerators()
   {
      return registry.getExportedInstances(ResourceGenerator.class);
   }

   @Override
   public ResourceFactory fireEvent(ResourceEvent event)
   {
      manager.fireEvent(event);
      return this;
   }

   /**
    * The methods below are unused until FORGE-801 is resolved
    */
   @Override
   public ResourceTransaction getCurrentTransaction()
   {
      return transaction;
   }

   @SuppressWarnings("unused")
   private <T> Resource<?> bindTransactionHook(Resource<?> result)
   {
      // Transaction Hook
      if (result != null && transaction != null)
      {
         result = transaction.decorateResource(result);
      }
      return result;
   }

   @Override
   public ResourceTransaction startTransaction() throws ResourceException
   {
      if (transaction != null)
      {
         throw new ResourceException("Transaction already exists!");
      }
      transaction = new ResourceTransactionImpl(this);
      return transaction;
   }

   void unsetTransaction()
   {
      transaction = null;
   }

}