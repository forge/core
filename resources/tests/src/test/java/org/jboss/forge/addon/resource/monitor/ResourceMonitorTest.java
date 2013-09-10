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
      DirectoryResource childDir = tempDirResource.getChildDirectory("child_dir");

      // NEW EVENT: ResourceCreated
      childDir.mkdir();

      FileResource<?> childFile = childDir.getChild("child_file.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated + ResourceModified of parent dir
      childFile.createNewFile();

      waitForMonitor();

      // NEW EVENT: ResourceDeleted
      childFile.delete();

      waitForMonitor();

      Assert.assertEquals(4, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceCreated.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceCreated.class)));
      Assert.assertThat(eventCollector.get(2), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(3), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceMonitorFile() throws Exception
   {
      File tempFile = File.createTempFile("resource_monitor", ".tmp");
      FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
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

      // NEW EVENT: ResourceModified
      resource.setContents("TEST");

      waitForMonitor();

      // NEW EVENT: ResourceDeleted
      resource.delete();

      waitForMonitor();

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
      FileResource<?> childFile = tempDirResource.getChild("child_file.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated + ResourceModified of parent dir
      childFile.createNewFile();

      childFile = tempDirResource.getChild("foo.txt").reify(FileResource.class);
      // NEW EVENT: ResourceCreated of parent dir
      childFile.createNewFile();

      waitForMonitor();

      // NEW EVENT: ResourceDeleted
      childFile.delete();

      waitForMonitor();

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
      FileResource<?> resource = resourceFactory.create(FileResource.class, tempFile);
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

      // NEW EVENT: ResourceModified
      resource.setContents("TEST");

      waitForMonitor();

      // NEW EVENT: ResourceDeleted
      resource.delete();

      waitForMonitor();

      Assert.assertEquals(2, eventCollector.size());
      Assert.assertThat(eventCollector.get(0), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(eventCollector.get(1), is(instanceOf(ResourceDeleted.class)));
      monitor.cancel();
   }

   private void waitForMonitor() throws InterruptedException
   {
      // Wait until the monitor detects changes
      Thread.sleep(INTERVAL);
   }

}
