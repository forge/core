/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.monitor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ResourceMonitorTest
{

   private static final long INTERVAL = 1000L;

   static
   {
      System.setProperty("resource.monitor.interval", String.valueOf(INTERVAL));
   }

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:facets"),
            @AddonDependency(name = "org.jboss.forge.addon:resources") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")
               );

      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Test(expected = IllegalArgumentException.class)
   public void testResourceMonitorShouldThrowIllegalArgumentOnNull() throws Exception
   {
      resourceFactory.monitor(null).cancel();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testResourceMonitorShouldThrowIllegalArgumentOnUnsupportedResource() throws Exception
   {
      URLResource resource = resourceFactory.create(URLResource.class, new URL("http://forge.jboss.org"));
      Assert.assertNotNull(resource);
      resourceFactory.monitor(resource).cancel();
   }

   @Test(expected = IllegalStateException.class)
   public void testResourceMonitorInexistentResourceShouldThrowIllegalStateException() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.delete();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      resourceFactory.monitor(tempDirResource).cancel();
   }

   @Test
   public void testResourceMonitorDirectory() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      ResourceMonitor monitor = resourceFactory.monitor(tempDirResource);
      final List<ResourceEvent> eventCollector = new ArrayList<ResourceEvent>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });

      final DirectoryResource childDir = tempDirResource.getChildDirectory("child_dir");

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceCreated
            childDir.mkdir();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 1;
         }
      }, 5, TimeUnit.SECONDS);
      final FileResource<?> childFile = childDir.getChild("child_file.txt").reify(FileResource.class);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // 2 NEW EVENTS: ResourceCreated & ResourceModified of parent dir
            childFile.createNewFile();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 3;
         }
      }, 5, TimeUnit.SECONDS);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceDeleted
            childFile.delete();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 5;
         }
      }, 5, TimeUnit.SECONDS);

      Assert.assertEquals(5, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceCreated.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(2), is(instanceOf(ResourceCreated.class)));
      Assert.assertThat(eventCollector.get(3), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(4), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFile() throws Exception
   {
      File tempFile = File.createTempFile("resource_monitor", ".tmp");
      final FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
      ResourceMonitor monitor = resourceFactory.monitor(resource);
      final List<ResourceEvent> eventCollector = new ArrayList<ResourceEvent>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceModified
            resource.setContents("TEST");
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 1;
         }
      }, 5, TimeUnit.SECONDS);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceDeleted
            resource.delete();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 2;
         }
      }, 5, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   @Test
   public void testResourceMonitorDirectoryWithFilter() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      ResourceMonitor monitor = resourceFactory.monitor(tempDirResource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return "foo.txt".equals(resource.getName());
         }
      });
      final List<ResourceEvent> eventCollector = new ArrayList<ResourceEvent>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });
      final FileResource<?> childFile1 = tempDirResource.getChild("child_file.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated + ResourceModified of parent dir
      childFile1.createNewFile();

      final FileResource<?> childFile2 = tempDirResource.getChild("foo.txt").reify(FileResource.class);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceCreated of parent dir
            childFile2.createNewFile();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 1;
         }
      }, 5, TimeUnit.SECONDS);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceDeleted
            childFile2.delete();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 2;
         }
      }, 5, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceCreated.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFileWithFilter() throws Exception
   {
      File tempFile = File.createTempFile("resource_monitor", ".tmp");
      final FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
      ResourceMonitor monitor = resourceFactory.monitor(resource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource.getName().startsWith("resource_monitor");
         }
      });

      final List<ResourceEvent> eventCollector = new ArrayList<ResourceEvent>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceModified
            resource.setContents("TEST");
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 1;
         }
      }, 5, TimeUnit.SECONDS);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceDeleted
            resource.delete();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 2;
         }
      }, 5, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   private void waitForMonitor(Callable<Void> task, Callable<Boolean> status, int quantity, TimeUnit unit)
            throws TimeoutException
   {
      try
      {
         task.call();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      long start = System.currentTimeMillis();
      while (!Callables.call(status))
      {
         if (System.currentTimeMillis() >= (start + TimeUnit.MILLISECONDS.convert(quantity, unit))
                  && !Callables.call(status))
         {
            throw new TimeoutException("Timeout occurred while waiting for status.");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException("Interrupted while waiting for status.", e);
         }
      }
   }

}
