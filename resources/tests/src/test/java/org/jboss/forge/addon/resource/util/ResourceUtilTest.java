/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.util;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
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
public class ResourceUtilTest
{
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
