/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.monitor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ResourceMonitorTest
{
   private ResourceFactory resourceFactory;
   private ResourceMonitor monitor;

   @Before
   public void setUp()
   {
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @After
   public void cancelMonitor()
   {
      if (monitor != null)
      {
         monitor.cancel();
         monitor = null;
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void testResourceMonitorShouldThrowIllegalArgumentOnNull() throws Exception
   {
      monitor = resourceFactory.monitor(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testResourceMonitorShouldThrowIllegalArgumentOnUnsupportedResource() throws Exception
   {
      URLResource resource = resourceFactory.create(URLResource.class, new URL("https://forge.jboss.org"));
      Assert.assertNotNull(resource);
      monitor = resourceFactory.monitor(resource);
   }

   @Test(expected = IllegalStateException.class)
   public void testResourceMonitorInexistentResourceShouldThrowIllegalStateException() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.delete();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      monitor = resourceFactory.monitor(tempDirResource);
   }

   @Test
   public void testResourceMonitorDirectory() throws Exception
   {
      Assume.assumeFalse("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      monitor = resourceFactory.monitor(tempDirResource);
      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
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
      }, 15, TimeUnit.SECONDS);
      final FileResource<?> childFile = childDir.getChild("child_file.txt").reify(FileResource.class);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceCreated
            childFile.createNewFile();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 2;
         }
      }, 15, TimeUnit.SECONDS);

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
            return eventCollector.size() == 3;
         }
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(3, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   public void testResourceMonitorDirectoryWindows() throws Exception
   {
      Assume.assumeTrue("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      monitor = resourceFactory.monitor(tempDirResource);
      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
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
      }, 15, TimeUnit.SECONDS);
      final FileResource<?> childFile = childDir.getChild("child_file.txt").reify(FileResource.class);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // NEW EVENT: ResourceCreated
            childFile.createNewFile();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 2;
         }
      }, 15, TimeUnit.SECONDS);

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
            return eventCollector.size() == 4;
         }
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(4, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceModified.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFile() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File tempFile = File.createTempFile("resource_monitor", ".tmp", tempDir);
      final FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
      monitor = resource.monitor();
      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
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
      }, 10, TimeUnit.SECONDS);

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
      }, 10, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Iterator<ResourceEvent> it = eventCollector.iterator();
      Assert.assertTrue(it.hasNext());
      Assert.assertThat(it.next(), is(instanceOf(ResourceModified.class)));
      Assert.assertTrue(it.hasNext());
      Assert.assertThat(it.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   public void testResourceMonitorDirectoryWithFilter() throws Exception
   {
      Assume.assumeFalse("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      monitor = resourceFactory.monitor(tempDirResource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return "foo.txt".equals(resource.getName());
         }
      });
      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });
      final FileResource<?> childFile1 = tempDirResource.getChild("child_file.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated
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
      }, 15, TimeUnit.SECONDS);

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
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   public void testResourceMonitorDirectoryWithFilterWindows() throws Exception
   {
      Assume.assumeTrue("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempDirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      monitor = resourceFactory.monitor(tempDirResource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return "foo.txt".equals(resource.getName());
         }
      });
      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            eventCollector.add(event);
         }
      });
      final FileResource<?> childFile1 = tempDirResource.getChild("child_file.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated
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
      }, 15, TimeUnit.SECONDS);

      waitForMonitor(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            // Windows 7 adds ResourceModified
            // NEW EVENT: ResourceDeleted
            childFile2.delete();
            return null;
         }
      }, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return eventCollector.size() == 3;
         }
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(3, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceCreated.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceModified.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFileWithFilter() throws Exception
   {
      Assume.assumeFalse("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      File tempFile = File.createTempFile("resource_monitor", ".tmp", tempDir);
      final FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
      monitor = resourceFactory.monitor(resource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource.getName().startsWith("resource_monitor");
         }
      });

      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
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
      }, 15, TimeUnit.SECONDS);

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
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceModified.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFileWithFilterWindows() throws Exception
   {
      Assume.assumeTrue("FORGE-1679", OperatingSystemUtils.isWindows());
      File tempDir = OperatingSystemUtils.createTempDir();
      File tempFile = File.createTempFile("resource_monitor", ".tmp", tempDir);
      final FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
      monitor = resourceFactory.monitor(resource, new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource.getName().startsWith("resource_monitor");
         }
      });

      final Set<ResourceEvent> eventCollector = new LinkedHashSet<>();
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
      }, 15, TimeUnit.SECONDS);

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
      }, 15, TimeUnit.SECONDS);

      Assert.assertEquals(2, eventCollector.size());
      Iterator<ResourceEvent> iterator = eventCollector.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceModified.class)));
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(ResourceDeleted.class)));
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
