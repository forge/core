/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Implementation of the {@link ResourceMonitor} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ResourceMonitorImpl implements ResourceMonitor
{
   private final FileAlterationMonitor monitor;
   private final FileAlterationObserver observer;
   private final DirectoryResource resource;
   private final ResourceFactory resourceFactory;

   public ResourceMonitorImpl(DirectoryResource resource, ResourceFactory resourceFactory,
            FileAlterationMonitor monitor,
            FileAlterationObserver observer)
   {
      super();
      this.resource = resource;
      this.resourceFactory = resourceFactory;
      this.monitor = monitor;
      this.observer = observer;
   }

   @Override
   public ListenerRegistration<ResourceListener> addResourceListener(
            final ResourceListener listener)
   {
      final FileAlterationListenerAdapter adapter = new FileAlterationListenerAdapter(resourceFactory, listener);
      observer.addListener(adapter);
      return new ListenerRegistration<ResourceListener>()
      {
         @Override
         public ResourceListener removeListener()
         {
            observer.removeListener(adapter);
            return listener;
         }
      };
   }

   @Override
   public DirectoryResource getResource()
   {
      return resource;
   }

   @Override
   public void cancel()
   {
      monitor.removeObserver(observer);
   }
}
