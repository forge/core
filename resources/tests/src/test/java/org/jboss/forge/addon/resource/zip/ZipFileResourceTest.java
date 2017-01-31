/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link ZipFileResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
@SuppressWarnings("unchecked")
public class ZipFileResourceTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment() throws Exception
   {
      AddonArchive addonArchive = ShrinkWrap.create(AddonArchive.class)
               .addAsResource(ZipFileResourceTest.class.getResource("encrypted.zip"), "encrypted.zip")
               .addAsServiceProvider(Service.class, ZipFileResourceTest.class);
      return addonArchive;
   }

   private ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testZipResource() throws Exception
   {
      File file = File.createTempFile("zipresource", ".zip");
      file.deleteOnExit();
      Resource<File> resource = resourceFactory.create(file);
      Assert.assertNotNull(resource);
      Assert.assertThat(resource, is(instanceOf(ZipFileResource.class)));
   }

   @Test
   public void testJarResource() throws Exception
   {
      File file = File.createTempFile("zipresource", ".jar");
      file.deleteOnExit();
      Resource<File> resource = resourceFactory.create(ZipFileResource.class, file);
      Assert.assertNotNull(resource);
      Assert.assertThat(resource, is(instanceOf(ZipFileResource.class)));
   }

   @Test
   public void testZipResourceAddFile() throws Exception
   {
      ZipFileResource resource = createTempZipFileResource();
      FileResource<?> internalResource = resourceFactory.create(FileResource.class, File.createTempFile("tmp", ".txt"));
      internalResource.deleteOnExit();
      internalResource.setContents("Hello World");
      resource.add(internalResource);
      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(1, children.size());
      Assert.assertEquals(internalResource.getName(), children.get(0).getName());
      Assert.assertEquals("Hello World", children.get(0).getContents());
   }

   @Test
   public void testZipResourceAddResource() throws Exception
   {
      ZipFileResource resource = createTempZipFileResource();
      FileResource<?> internalResource = resourceFactory.create(FileResource.class, File.createTempFile("tmp", ".txt"));
      internalResource.deleteOnExit();
      internalResource.setContents("Hello World");
      resource.add("myfile.txt", internalResource);
      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(1, children.size());
      Assert.assertEquals("myfile.txt", children.get(0).getName());
      Assert.assertEquals("Hello World", children.get(0).getContents());
   }

   @Test
   public void testZipResourceExtract() throws Exception
   {
      ZipFileResource resource = createTempZipFileResource();
      FileResource<?> internalResource = resourceFactory.create(FileResource.class, File.createTempFile("tmp", ".txt"));
      internalResource.deleteOnExit();
      internalResource.setContents("Hello World");
      resource.add(internalResource);

      DirectoryResource tmpDir = resourceFactory.create(OperatingSystemUtils.createTempDir())
               .reify(DirectoryResource.class);
      resource.extractTo(tmpDir);
      List<Resource<?>> children = tmpDir.listResources();
      Assert.assertEquals(1, children.size());
      Assert.assertEquals(internalResource.getName(), children.get(0).getName());
      Assert.assertEquals("Hello World", children.get(0).getContents());
   }

   @Test
   public void testZipFileResourceReadEncrypted() throws Exception
   {
      InputStream encryptedStream = getClass().getClassLoader().getResourceAsStream("encrypted.zip");
      File zipFile = File.createTempFile("zipresource", ".zip");
      try (FileOutputStream fos = new FileOutputStream(zipFile))
      {
         Streams.write(encryptedStream, fos);
      }
      ZipFileResource resource = resourceFactory.create(ZipFileResource.class, zipFile);
      Assert.assertTrue(resource.isEncrypted());
      resource.setPassword("password".toCharArray());
      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(1, children.size());
      Assert.assertEquals("content.txt", children.get(0).getName());
      Assert.assertThat(children.get(0).getContents(), equalTo("Hello World\n"));
   }

   @Test
   public void testZipFileResourceAddDirectory() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      tmpDir.deleteOnExit();
      File child1 = new File(tmpDir, "child1.txt");
      child1.deleteOnExit();
      Files.write(child1.toPath(), "Child 1".getBytes());
      File child2 = new File(tmpDir, "child2.txt");
      child2.deleteOnExit();
      Files.write(child2.toPath(), "Child 2".getBytes());

      DirectoryResource directoryResource = resourceFactory.create(DirectoryResource.class, tmpDir);
      ZipFileResource resource = createTempZipFileResource();
      resource.add(directoryResource);

      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(3, children.size());
      Assert.assertTrue(((ZipFileResourceEntry) children.get(0)).isDirectory());
      Assert.assertEquals(tmpDir.getName() + File.separatorChar, children.get(0).getName());
      Assert.assertEquals(tmpDir.getName() + File.separatorChar + child1.getName(), children.get(1).getName());
      Assert.assertEquals(tmpDir.getName() + File.separatorChar + child2.getName(), children.get(2).getName());
   }

   @Test
   public void testZipFileResourceAddCustomDirectoryName() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      tmpDir.deleteOnExit();
      File child1 = new File(tmpDir, "child1.txt");
      child1.deleteOnExit();
      Files.write(child1.toPath(), "Child 1".getBytes());
      File child2 = new File(tmpDir, "child2.txt");
      child2.deleteOnExit();
      Files.write(child2.toPath(), "Child 2".getBytes());

      DirectoryResource directoryResource = resourceFactory.create(DirectoryResource.class, tmpDir);
      ZipFileResource resource = createTempZipFileResource();
      resource.add("my-new-directory", directoryResource);

      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(2, children.size());
      Assert.assertFalse(((ZipFileResourceEntry) children.get(0)).isDirectory());
      Assert.assertFalse(((ZipFileResourceEntry) children.get(1)).isDirectory());
      Assert.assertEquals("my-new-directory" + File.separatorChar + child1.getName(), children.get(0).getName());
      Assert.assertEquals("my-new-directory" + File.separatorChar + child2.getName(), children.get(1).getName());
   }

   @Test
   public void testExtractZipFileResourceEntry() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      tmpDir.deleteOnExit();
      File child1 = new File(tmpDir, "child1.txt");
      child1.deleteOnExit();
      Files.write(child1.toPath(), "Child 1".getBytes());
      File child2 = new File(tmpDir, "child2.txt");
      child2.deleteOnExit();
      Files.write(child2.toPath(), "Child 2".getBytes());

      DirectoryResource directoryResource = resourceFactory.create(DirectoryResource.class, tmpDir);
      DirectoryResource newDirectoryResource = resourceFactory.create(DirectoryResource.class,
               OperatingSystemUtils.createTempDir());
      ZipFileResource resource = createTempZipFileResource();
      resource.add("my-new-directory", directoryResource);

      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(2, children.size());

      ZipFileResourceEntry entry = (ZipFileResourceEntry) children.get(1);
      entry.extractTo(newDirectoryResource);

      List<Resource<?>> newChildren = newDirectoryResource.listResources();
      Assert.assertEquals(1, newChildren.size());
      Assert.assertThat(newChildren.get(0), is(instanceOf(DirectoryResource.class)));
      Assert.assertEquals("my-new-directory", newChildren.get(0).getName());
   }

   @Test
   public void testExtractZipFileResourceEntryCustomName() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      tmpDir.deleteOnExit();
      File child1 = new File(tmpDir, "child1.txt");
      child1.deleteOnExit();
      Files.write(child1.toPath(), "Child 1".getBytes());
      File child2 = new File(tmpDir, "child2.txt");
      child2.deleteOnExit();
      Files.write(child2.toPath(), "Child 2".getBytes());

      DirectoryResource directoryResource = resourceFactory.create(DirectoryResource.class, tmpDir);
      DirectoryResource newDirectoryResource = resourceFactory.create(DirectoryResource.class,
               OperatingSystemUtils.createTempDir());
      ZipFileResource resource = createTempZipFileResource();
      resource.add("my-new-directory", directoryResource);

      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(2, children.size());

      ZipFileResourceEntry entry = (ZipFileResourceEntry) children.get(1);
      entry.extractTo(newDirectoryResource, "new-file.txt");

      List<Resource<?>> newChildren = newDirectoryResource.listResources();
      Assert.assertEquals(1, newChildren.size());
      Assert.assertThat(newChildren.get(0), is(instanceOf(FileResource.class)));
      Assert.assertThat(newChildren.get(0), is(not(instanceOf(DirectoryResource.class))));
      Assert.assertEquals("new-file.txt", newChildren.get(0).getName());
   }

   @Test
   public void testExtractZipFileResourceEntryCustomDirectoryName() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      tmpDir.deleteOnExit();
      File child = new File(tmpDir, "child");
      child.mkdir();
      child.deleteOnExit();
      File child2 = new File(child, "grandchild.txt");
      child2.deleteOnExit();
      Files.write(child2.toPath(), "Child 2".getBytes());

      DirectoryResource directoryResource = resourceFactory.create(DirectoryResource.class, tmpDir);

      DirectoryResource newDirectoryResource = resourceFactory.create(DirectoryResource.class,
               OperatingSystemUtils.createTempDir());
      ZipFileResource resource = createTempZipFileResource();
      resource.add("my-new-directory", directoryResource);

      List<Resource<?>> children = resource.listResources();
      Assert.assertEquals(1, children.size());

      ZipFileResourceEntry entry = (ZipFileResourceEntry) children.get(0);
      entry.extractTo(newDirectoryResource, "new-file.txt");

      List<Resource<?>> newChildren = newDirectoryResource.listResources();
      Assert.assertEquals(1, newChildren.size());
      Assert.assertThat(newChildren.get(0), is(instanceOf(FileResource.class)));
      Assert.assertThat(newChildren.get(0), is(not(instanceOf(DirectoryResource.class))));
      Assert.assertEquals("new-file.txt", newChildren.get(0).getName());
   }

   private ZipFileResource createTempZipFileResource() throws IOException
   {
      ZipFileResource resource = resourceFactory.create(File.createTempFile("zipresource", ".zip"))
               .reify(ZipFileResource.class);
      resource.delete();
      resource.deleteOnExit();
      return resource;
   }

}
