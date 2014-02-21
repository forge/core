/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;

/**
 * This {@link FileMonitor} uses commons-io to listen for changes in files
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class FileMonitor
{
   private FileWatcher watcher;

   void init(@Observes @Local PostStartup postStartup) throws Exception
   {
      watcher = new FileWatcher();
      watcher.start();
   }

   void destroy(@Observes @Local PreShutdown preShutdown) throws Exception
   {
      if (watcher != null)
      {
         watcher.stop();
         watcher = null;
      }
   }

   public ResourceMonitor registerMonitor(final ResourceFactory resourceFactory, final FileResource<?> resource,
            final ResourceFilter resourceFilter)
   {
      if (watcher == null)
      {
         throw new IllegalStateException("File Monitor is not started yet");
      }
      DirectoryResource dirResource = resource.reify(DirectoryResource.class);
      ResourceFilter filter = resourceFilter;
      if (dirResource == null)
      {
         // It's a file, monitor the parent and add a filter to the file
         dirResource = resource.getParent();
         filter = new ResourceFilter()
         {
            @Override
            public boolean accept(Resource<?> type)
            {
               boolean isMonitoredFile = type.equals(resource);
               if (!isMonitoredFile)
               {
                  return false;
               }
               else if (resourceFilter != null)
               {
                  return resourceFilter.accept(type);
               }
               else
               {
                  return true;
               }
            }
         };
      }
      ResourceMonitorImpl resourceMonitor = new ResourceMonitorImpl(this, dirResource, resourceFactory, filter);
      try
      {
         watcher.register(resourceMonitor);
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Could not register resource monitor", e);
      }
      return resourceMonitor;
   }

   void cancel(ResourceMonitorImpl monitor)
   {
      watcher.unregister(monitor);
   }
}
