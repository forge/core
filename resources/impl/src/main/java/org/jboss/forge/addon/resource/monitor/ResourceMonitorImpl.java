/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.resource.PathResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;

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

   private final Resource<?> resource;
   private final Set<ResourceListener> listeners = new LinkedHashSet<>();
   private final ResourceFactory resourceFactory;
   private final ResourceFilter resourceFilter;
   private final boolean resourceIsFile;

   public ResourceMonitorImpl(FileMonitor fileMonitor, Resource<?> resource, ResourceFactory resourceFactory,
            ResourceFilter resourceFilter)
   {
      Assert.isTrue(resource instanceof DirectoryResource || resource instanceof PathResource,
               "resource parameter must be either DirectoryResource or PathResource");

      this.resourceIsFile = resource instanceof FileResource;
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
      Object underlyingObject = (resourceIsFile) ? path.toFile() : path;
      Resource<?> resource = resourceFactory.create(underlyingObject);
      fireEvent(new ResourceModified(resource));
   }

   void onPathCreate(Path path)
   {
      Object underlyingObject = (resourceIsFile) ? path.toFile() : path;
      Resource<?> resource = resourceFactory.create(underlyingObject);
      fireEvent(new ResourceCreated(resource));
   }

   void onPathDelete(Path path)
   {
      Object underlyingObject = (resourceIsFile) ? path.toFile() : path;
      Resource<?> resource = resourceFactory.create(underlyingObject);
      fireEvent(new ResourceDeleted(resource));
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
   public Resource<?> getResource()
   {
      return resource;
   }

   Path getResourcePath()
   {
      if (resource instanceof DirectoryResource)
      {
         return ((DirectoryResource) resource).getUnderlyingResourceObject().toPath();
      }
      else if (resource instanceof PathResource)
      {
         return (Path) ((PathResource<?>) resource).getUnderlyingResourceObject();
      }
      else
      {
         throw new IllegalStateException("Invalid resource type");
      }
   }

   @Override
   public void cancel()
   {
      fileMonitor.cancel(this);
      listeners.clear();
   }
}
