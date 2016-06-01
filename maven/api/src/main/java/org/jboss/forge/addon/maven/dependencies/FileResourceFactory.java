/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.dependencies;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.DefaultFileOperations;
import org.jboss.forge.addon.resource.FileOperations;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Simple {@link ResourceFactory} for working outside of a container environment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class FileResourceFactory implements ResourceFactory
{
   @Override
   @SuppressWarnings("unchecked")
   public <E, T extends Resource<E>> T create(Class<T> type, E underlyingResource)
   {
      return (T) create(underlyingResource);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <E> Resource<E> create(E underlyingResource)
   {
      if (underlyingResource instanceof File)
         return (Resource<E>) createFileResource((File) underlyingResource);
      return null;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private <E> Resource<E> createFileResource(File resource)
   {
      return new AbstractFileResource(this, resource)
      {
         @Override
         public Resource createFrom(File file)
         {
            return createFileResource(file);
         }

         @Override
         protected List<File> doListResources()
         {
            return Collections.emptyList();
         }
      };
   }

   @Override
   public ResourceMonitor monitor(Resource<?> resource)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResourceMonitor monitor(Resource<?> resource, ResourceFilter resourceFilter)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResourceTransaction getTransaction()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public FileOperations getFileOperations()
   {
      return DefaultFileOperations.INSTANCE;
   }

   @Override
   public ListenerRegistration<ResourceTransactionListener> addTransactionListener(ResourceTransactionListener listener)
   {
      throw new UnsupportedOperationException();
   }
}
