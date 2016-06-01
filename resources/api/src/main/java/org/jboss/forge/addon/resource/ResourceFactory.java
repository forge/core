/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.File;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ResourceFactory
{
   /**
    * Create a {@link Resource} of the given type, using the provided underlying resource instance.
    * 
    * @return <code>null</code> if no resource could be created for the given object.
    */
   <E, T extends Resource<E>> T create(Class<T> type, E underlyingResource);

   /**
    * Create a {@link Resource} to represent the provided underlying resource. The resource type will be detected
    * automatically.
    * 
    * @return <code>null</code> if no resource could be created for the given object.
    */
   <E> Resource<E> create(E underlyingResource);

   /**
    * Monitors a specific resource for changes and fires the registered listeners
    * 
    * @param resource the resource to be monitored
    * @return a {@link ResourceMonitor} for the specific resource
    */
   ResourceMonitor monitor(Resource<?> resource);

   /**
    * Monitors a specific resource for changes and fires the registered listeners given the specified filter
    * 
    * @param resource the resource to be monitored
    * @param resourceFilter a filter for children of the specified resource
    * @return a {@link ResourceMonitor} for the specific resource
    */
   ResourceMonitor monitor(Resource<?> resource, ResourceFilter resourceFilter);

   /**
    * Get the transaction associated with this {@link ResourceFactory} in the calling thread
    * 
    * @return the {@link ResourceTransaction} associated with this factory. Throws {@link UnsupportedOperationException}
    *         if the implementation does not support it.
    */
   ResourceTransaction getTransaction();

   /**
    * Returns the operational layer for {@link File} objects. This object should be used if operations on a {@link File}
    * should happen in a transactional context.
    * 
    * The default implementation invokes the {@link File} methods where applicable
    */
   FileOperations getFileOperations();

   /**
    * Add a {@link ResourceTransactionListener} to be notified when {@link ResourceTransaction} events occur.
    */
   ListenerRegistration<ResourceTransactionListener> addTransactionListener(ResourceTransactionListener listener);
}