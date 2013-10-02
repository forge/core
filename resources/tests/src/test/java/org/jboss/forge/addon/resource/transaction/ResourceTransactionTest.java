/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.transaction;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Resource Transaction
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ResourceTransactionTest
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
   @SuppressWarnings("unchecked")
   public void testResourceTransactionCommit() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File file = File.createTempFile("fileresourcetest", ".tmp", tempDir);
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertNotNull(transaction);
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);
      fileResource.setContents("Hello World");
      transaction.commit();
      Assert.assertTrue(fileResource.exists());
      Assert.assertEquals("Hello World", fileResource.getContents());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceTransactionRollback() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File file = File.createTempFile("fileresourcetest", ".tmp", tempDir);
      file.delete();
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertNotNull(transaction);
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);
      fileResource.setContents("Hello World");
      Assert.assertTrue(fileResource.exists());
      Assert.assertFalse(file.exists());
      transaction.rollback();
      Assert.assertFalse(fileResource.exists());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceChangeSet() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File file = File.createTempFile("fileresourcetest", ".tmp", tempDir);
      file.delete();
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertNotNull(transaction);
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);
      fileResource.setContents("Hello World");
      List<ResourceEvent> changeSet = transaction.getChangeSet();
      Assert.assertEquals(3, changeSet.size());
      // Created the file
      Assert.assertThat(changeSet.get(0), is(instanceOf(ResourceCreated.class)));
      Assert.assertEquals(fileResource, changeSet.get(0).getResource());
      // Modified the Directory
      Assert.assertThat(changeSet.get(1), is(instanceOf(ResourceModified.class)));
      Assert.assertThat(changeSet.get(1).getResource(), is(instanceOf(DirectoryResource.class)));
      // Modified the File resource
      Assert.assertThat(changeSet.get(2), is(instanceOf(ResourceModified.class)));
      Assert.assertEquals(fileResource, changeSet.get(2).getResource());
      Assert.assertFalse(file.exists());
      Assert.assertTrue(fileResource.exists());
      transaction.commit();
      Assert.assertTrue(fileResource.exists());
      Assert.assertTrue(file.exists());
      Assert.assertEquals("Hello World", fileResource.getContents());
   }

   @After
   public void tearDown()
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      if (transaction.isStarted())
         transaction.rollback();
   }

}
