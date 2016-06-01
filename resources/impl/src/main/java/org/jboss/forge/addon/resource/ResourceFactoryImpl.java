/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.util.Set;
import java.util.TreeMap;

import org.jboss.forge.addon.resource.monitor.FileMonitor;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionImpl;
import org.jboss.forge.addon.resource.transaction.file.FileResourceTransactionManager;
import org.jboss.forge.addon.resource.util.RelatedClassComparator;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Sets;

/**
 * Implementation of {@link ResourceFactory}
 * 
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * @author Mike Brock <cbrock@redhat.com>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ResourceFactoryImpl implements ResourceFactory
{
   private final Set<ResourceGenerator> generators = Sets.getConcurrentSet();
   private volatile long version = -1;

   @Override
   public <E, T extends Resource<E>> T create(final Class<T> type, final E underlyingResource)
   {
      T result = null;
      TreeMap<Class<?>, ResourceGenerator> generated = new TreeMap<>(new RelatedClassComparator());

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
         if (generated.size() > 0)
         {
            result = (T) generated.lastEntry().getValue().getResource(this, type, underlyingResource);
         }
      }
      return result;
   }

   private Iterable<ResourceGenerator> getGenerators()
   {
      if (getAddonRegistry().getVersion() != version)
      {
         version = getAddonRegistry().getVersion();
         generators.clear();
         for (ResourceGenerator generator : getAddonRegistry().getServices(ResourceGenerator.class))
         {
            generators.add(generator);
         }
      }
      return generators;
   }

   @Override
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
      return getFileMonitor().registerMonitor(this, fileResource, resourceFilter);
   }

   @Override
   public FileResourceTransactionImpl getTransaction()
   {
      return getTransactionManager().getCurrentTransaction(this);
   }

   @Override
   public FileOperations getFileOperations()
   {
      FileResourceTransactionImpl transaction = getTransaction();
      if (transaction.isStarted())
      {
         return transaction;
      }
      else
      {
         return DefaultFileOperations.INSTANCE;
      }
   }

   @Override
   public ListenerRegistration<ResourceTransactionListener> addTransactionListener(ResourceTransactionListener listener)
   {
      return getTransactionManager().addTransactionListener(listener);
   }

   private AddonRegistry getAddonRegistry()
   {
      return SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
   }

   private FileMonitor getFileMonitor()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), FileMonitor.class).get();
   }

   private FileResourceTransactionManager getTransactionManager()
   {
      return SimpleContainer
               .getServices(getClass().getClassLoader(), FileResourceTransactionManager.class).get();
   }

}