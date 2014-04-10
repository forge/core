/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.monitor.FileMonitor;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionImpl;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionManager;
import org.jboss.forge.addon.resource.util.RelatedClassComparator;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

/**
 * Implementation of {@link ResourceFactory}
 * 
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * @author Mike Brock <cbrock@redhat.com>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ResourceFactoryImpl implements ResourceFactory
{
   @Inject
   private AddonRegistry registry;

   @Inject
   private FileMonitor fileMonitor;

   @Inject
   private FileResourceTransactionManager transactionManager;

   @SuppressWarnings("rawtypes")
   private final Set<ResourceGenerator> generators = new HashSet<>();
   private long version = -1;

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      T result = null;
      synchronized (this)
      {
         TreeMap<Class<?>, ResourceGenerator> generated = new TreeMap<>(
                  new RelatedClassComparator());

         for (ResourceGenerator generator : getGenerators())
         {
            if (generator.handles(type, underlyingResource))
            {
               Class resourceType = generator.getResourceType(this, type, underlyingResource);
               if (type.isAssignableFrom(resourceType))
               {
                  generated.put(resourceType, generator);
               }
            }
         }
         if (generated.size() > 0)
         {
            result = (T) generated.lastEntry().getValue().getResource(this, type, underlyingResource);
         }
      }
      return result;
   }

   @SuppressWarnings("rawtypes")
   private Iterable<ResourceGenerator> getGenerators()
   {
      if (registry.getVersion() != version)
      {
         version = registry.getVersion();
         generators.clear();
         for (ResourceGenerator generator : registry.getServices(ResourceGenerator.class))
         {
            generators.add(generator);
         }
      }
      return generators;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <E> Resource<E> create(E underlyingResource)
   {
      return create(Resource.class, underlyingResource);
   }

   @Override
   public ResourceMonitor monitor(Resource<?> resource)
   {
      return monitor(resource, null);
   }

   @Override
   public ResourceMonitor monitor(Resource<?> resource, ResourceFilter resourceFilter)
   {
      Assert.notNull(resource, "Resource cannot be null");

      if (!resource.exists())
      {
         throw new IllegalStateException("Resource must exist to be monitored");
      }

      if (resource instanceof FileResource)
      {
         return fileMonitor.registerMonitor(this, (FileResource<?>) resource, resourceFilter);
      }
      else if (resource instanceof PathResource)
      {
         return fileMonitor.registerMonitor(this, (PathResource<?>) resource, resourceFilter);
      }
      else
      {
         throw new IllegalArgumentException("Resource must be a FileResource or PathResource, was "
                  + resource.getClass().getName());
      }
   }

   @Override
   public FileResourceTransactionImpl getTransaction()
   {
      return transactionManager.getCurrentTransaction(this);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> ResourceOperations<T> getResourceOperations(Class<T> type)
   {
      if (File.class.equals(type))
      {
         FileResourceTransactionImpl transaction = getTransaction();
         if (transaction.isStarted())
         {
            return (ResourceOperations<T>) transaction;
         }
         else
         {
            return (ResourceOperations<T>) DefaultFileOperations.INSTANCE;
         }
      }
      else if (Path.class.equals(type))
      {
         return (ResourceOperations<T>) DefaultPathOperations.INSTANCE;
      }
      else
      {
         throw new IllegalArgumentException("Unsupport resource type [" + type.getName() + "]");
      }
   }

   @Override
   public ListenerRegistration<ResourceTransactionListener> addTransactionListener(ResourceTransactionListener listener)
   {
      return transactionManager.addTransactionListener(listener);
   }
}