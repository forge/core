/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.monitor;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Implementation of the {@link ResourceMonitor} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */

@SuppressWarnings("unchecked")
public class ResourceMonitorImpl implements ResourceMonitor
{
   private static final Logger log = Logger.getLogger(ResourceMonitorImpl.class.getName());

   private final FileMonitor fileMonitor;

   private final DirectoryResource resource;
   private final Set<ResourceListener> listeners = new LinkedHashSet<>();
   private final ResourceFactory resourceFactory;
   private final ResourceFilter resourceFilter;

   public ResourceMonitorImpl(FileMonitor fileMonitor, DirectoryResource resource, ResourceFactory resourceFactory,
            ResourceFilter resourceFilter)
   {
      this.fileMonitor = fileMonitor;
      this.resource = resource;
      this.resourceFactory = resourceFactory;
      this.resourceFilter = resourceFilter;
   }

   @Override
   public ListenerRegistration<ResourceListener> addResourceListener(
            final ResourceListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration<ResourceListener>()
      {
         @Override
         public ResourceListener removeListener()
         {
            listeners.remove(listener);
            return listener;
         }
      };
   }

   void onPathModify(Path path)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, path.toFile());
      fireEvent(new ResourceModified(fileResource));
   }

   void onPathCreate(Path path)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, path.toFile());
      fireEvent(new ResourceCreated(fileResource));
   }

   void onPathDelete(Path path)
   {
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, path.toFile());
      fireEvent(new ResourceDeleted(fileResource));
   }

   private void fireEvent(ResourceEvent event)
   {
      if (resourceFilter == null || resourceFilter.accept(event.getResource()))
      {
         for (ResourceListener listener : listeners)
         {
            try
            {
               listener.processEvent(event);
            }
            catch (Exception e)
            {
               log.log(Level.SEVERE, "Error while firing listener", e);
            }
         }
      }
   }

   @Override
   public DirectoryResource getResource()
   {
      return resource;
   }

   Path getResourcePath()
   {
      return resource.getUnderlyingResourceObject().toPath();
   }

   @Override
   public void cancel()
   {
      fileMonitor.cancel(this);
      listeners.clear();
   }
}
