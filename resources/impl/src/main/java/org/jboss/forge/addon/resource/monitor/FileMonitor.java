/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import java.io.File;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
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
   private static final long CHECK_INTERVAL = Long.getLong("resource.monitor.interval", 5000L);

   private Logger log = Logger.getLogger(getClass().getName());
   private FileAlterationMonitor alterationMonitor;

   public FileMonitor()
   {
      alterationMonitor = new FileAlterationMonitor(CHECK_INTERVAL);
      alterationMonitor.setThreadFactory(new ThreadFactory()
      {
         @Override
         public Thread newThread(Runnable r)
         {
            return new Thread(r, "Resource File Monitor");
         }
      });
   }

   void init(@Observes PostStartup postStartup) throws Exception
   {
      alterationMonitor.start();
   }

   void destroy(@Observes PreShutdown preShutdown) throws Exception
   {
      alterationMonitor.stop();
   }

   public ResourceMonitor registerMonitor(final ResourceFactory resourceFactory, final FileResource<?> resource,
            final ResourceFilter resourceFilter)
   {
      final DirectoryResource dirResource;
      IOFileFilter filter;
      if (resource instanceof DirectoryResource)
      {
         dirResource = (DirectoryResource) resource;
         filter = null;
      }
      else
      {
         dirResource = resource.getParent();
         filter = FileFilterUtils.nameFileFilter(resource.getName());
      }
      if (resourceFilter != null)
      {
         FileFilterResourceAdapter adapter = new FileFilterResourceAdapter(resourceFactory, resourceFilter);
         if (filter == null)
         {
            filter = adapter;
         }
         else
         {
            filter = FileFilterUtils.and(filter, adapter);
         }
      }
      File directory = dirResource.getUnderlyingResourceObject();
      FileAlterationObserver observer = new FileAlterationObserver(directory, filter);
      try
      {
         observer.initialize();
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Error while initializing File observer", e);
      }
      alterationMonitor.addObserver(observer);
      return new ResourceMonitorImpl(dirResource, resourceFactory, alterationMonitor, observer);
   }
}
