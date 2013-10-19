/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.monitor.FileMonitor;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionImpl;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionManager;
import org.jboss.forge.addon.resource.util.RelatedClassComparator;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
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

   private Imported<ResourceGenerator<?, ?>> instances;

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      T result = null;
      synchronized (this)
      {
         TreeMap<Class<?>, ResourceGenerator> generated = new TreeMap<Class<?>, ResourceGenerator>(
                  new RelatedClassComparator());

         // FIXME Workaround for FORGE-1263
         if (instances == null)
            instances = (Imported) registry.getServices(ResourceGenerator.class);

         for (ResourceGenerator generator : instances)
         {
            if (generator.handles(type, underlyingResource))
            {
               Class resourceType = generator.getResourceType(type, underlyingResource);
               if (type.isAssignableFrom(resourceType))
               {
                  generated.put(resourceType, generator);
               }
            }
            instances.release(generator);
         }
         if (generated.size() > 0)
         {
            result = (T) generated.lastEntry().getValue().getResource(this, type, underlyingResource);
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

   @Override
   public ResourceMonitor monitor(Resource<?> resource)
   {
      return monitor(resource, null);
   }

   @Override
   public ResourceMonitor monitor(Resource<?> resource, ResourceFilter resourceFilter)
   {
      Assert.notNull(resource, "Resource cannot be null");
      Assert.isTrue(resource instanceof FileResource, "Resource must be a FileResource, was "
               + resource.getClass().getName());
      if (!resource.exists())
      {
         throw new IllegalStateException("Resource must exist to be monitored");
      }
      FileResource<?> fileResource = (FileResource<?>) resource;
      return fileMonitor.registerMonitor(this, fileResource, resourceFilter);
   }

   @Override
   public FileResourceTransactionImpl getTransaction()
   {
      return transactionManager.getCurrentTransaction(this);
   }

   @Override
   public FileResourceOperations getFileOperations()
   {
      FileResourceTransactionImpl transaction = getTransaction();
      if (transaction.isStarted())
      {
         return transaction;
      }
      else
      {
         return DefaultFileResourceOperations.INSTANCE;
      }
   }

}