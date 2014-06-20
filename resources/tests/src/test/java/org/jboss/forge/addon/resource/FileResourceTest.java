/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.io.Files;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FileResourceTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")
               );

      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Test
   @SuppressWarnings("unchecked")
   public void testDirectoryResourceReifyShouldRetunNullForFiles() throws IOException
   {
      File file = File.createTempFile("fileresourcetest", ".tmp");
      file.deleteOnExit();
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);
      Assert.assertNull(fileResource.reify(DirectoryResource.class));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceOutputStream() throws IOException
   {
      File file = File.createTempFile("fileresourcetest", ".tmp");
      file.deleteOnExit();
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      try (OutputStream os = fileResource.getResourceOutputStream())
      {
         os.write("CONTENT".getBytes());
         os.flush();
      }
      String fileContent = Files.readFirstLine(file, Charset.defaultCharset());
      Assert.assertEquals("CONTENT", fileContent);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testFileResourceGetChildReturnsNull() throws IOException
   {
      File file = File.createTempFile("fileresourcetest", ".tmp");
      file.deleteOnExit();
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNull(fileResource.getChild("foo"));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testDirectoryResourceResolveShouldBeTheSameForChild() throws IOException
   {
      File file = File.createTempFile("fileresourcetest", ".tmp");
      file.deleteOnExit();
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      FileResource<?> parentResource = resourceFactory.create(FileResource.class, file.getParentFile());
      Assert.assertNotNull(fileResource);
      Assert.assertNotNull(parentResource);
      List<Resource<?>> children = parentResource.resolveChildren(file.getName());
      Assert.assertEquals(1, children.size());
      Assert.assertEquals(fileResource, children.get(0));
   }

}
