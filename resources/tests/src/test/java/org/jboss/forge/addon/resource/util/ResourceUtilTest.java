/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.io.File;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ResourceUtilTest
{
   private ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testContextFile() throws Exception
   {
      File tempFile = File.createTempFile("resourceutiltest", ".tmp");
      tempFile.deleteOnExit();
      Resource<File> resource = resourceFactory.create(tempFile);
      Assert.assertEquals(tempFile, ResourceUtil.getContextFile(resource));
   }

   @Test
   public void testContextDirectory() throws Exception
   {
      File tempDirectory = OperatingSystemUtils.getTempDirectory();
      Resource<File> resource = resourceFactory.create(tempDirectory);
      Assert.assertEquals(resource, ResourceUtil.getContextDirectory(resource));

      File tempFile = File.createTempFile("resourceutiltest", ".tmp");
      tempFile.deleteOnExit();
      Resource<File> fileResource = resourceFactory.create(tempFile);
      Assert.assertEquals(fileResource.getParent(), ResourceUtil.getContextDirectory(resource));
   }

   @Test
   public void testIsChildOf() throws Exception
   {
      File tempDirectory = OperatingSystemUtils.getTempDirectory();
      File tempFile = File.createTempFile("resourceutiltest", ".tmp", tempDirectory);
      tempFile.deleteOnExit();

      Resource<File> directoryResource = resourceFactory.create(tempDirectory);
      Resource<File> fileResource = resourceFactory.create(tempFile);
      Assert.assertTrue(ResourceUtil.isChildOf(directoryResource, fileResource));
      Assert.assertFalse(ResourceUtil.isChildOf(fileResource, directoryResource));
   }

}
