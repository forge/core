/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceCreated;
import org.jboss.forge.addon.resource.events.ResourceDeleted;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.events.ResourceModified;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
   private ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @After
   public void tearDown()
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      if (transaction.isStarted())
         transaction.rollback();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceTransactionCommit() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File file = createTempFile(tempDir, false);
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
      File file = createTempFile(tempDir, true);
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
      File file = createTempFile(tempDir, true);
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertNotNull(transaction);
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);
      fileResource.setContents("Hello World");
      Assert.assertEquals("Hello World", fileResource.getContents());
      Collection<ResourceEvent> changeSet = transaction.getChangeSet();
      Assert.assertEquals(3, changeSet.size());
      Iterator<ResourceEvent> iterator = changeSet.iterator();
      // Created the file
      {
         ResourceEvent event = iterator.next();
         Assert.assertThat(event, is(instanceOf(ResourceCreated.class)));
         Assert.assertEquals(fileResource, event.getResource());
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the Directory
         Assert.assertThat(event, is(instanceOf(ResourceModified.class)));
         Assert.assertThat(event.getResource(), is(instanceOf(DirectoryResource.class)));
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the File resource
         Assert.assertThat(event, is(instanceOf(ResourceModified.class)));
         Assert.assertEquals(fileResource, event.getResource());
      }
      Assert.assertFalse(file.exists());
      Assert.assertTrue(fileResource.exists());
      transaction.commit();
      Assert.assertTrue(fileResource.exists());
      Assert.assertTrue(file.exists());
      Assert.assertEquals("Hello World", fileResource.getContents());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testResourceChangeSetFromBlog() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      File file = createTempFile(tempDir, true);
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertNotNull(transaction);
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      Assert.assertNotNull(fileResource);

      fileResource.setContents("Hello World");
      Assert.assertEquals("Hello World", fileResource.getContents());

      FileResource<?> anotherResource = resourceFactory.create(FileResource.class, createTempFile(tempDir, false));
      // The file won't be deleted until commit is performed
      anotherResource.delete();

      FileResource<?> newResource = resourceFactory.create(FileResource.class, createTempFile(tempDir, true));
      // The file won't be created until commit is performed
      newResource.createNewFile();
      Collection<ResourceEvent> changeSet = transaction.getChangeSet();
      Assert.assertEquals(5, changeSet.size());
      Iterator<ResourceEvent> iterator = changeSet.iterator();
      {
         ResourceEvent event = iterator.next();
         // Created the file
         Assert.assertThat(event, is(instanceOf(ResourceCreated.class)));
         Assert.assertEquals(fileResource, event.getResource());
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the Directory
         Assert.assertThat(event, is(instanceOf(ResourceModified.class)));
         Assert.assertThat(event.getResource(), is(instanceOf(DirectoryResource.class)));
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the Directory
         Assert.assertThat(event, is(instanceOf(ResourceModified.class)));
         Assert.assertEquals(fileResource, event.getResource());
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the File resource
         Assert.assertThat(event, is(instanceOf(ResourceDeleted.class)));
         Assert.assertEquals(anotherResource, event.getResource());
      }
      {
         ResourceEvent event = iterator.next();
         // Modified the File resource
         Assert.assertThat(event, is(instanceOf(ResourceCreated.class)));
         Assert.assertEquals(newResource, event.getResource());
      }
      Assert.assertFalse(file.exists());
      Assert.assertTrue(fileResource.exists());
      transaction.commit();
      Assert.assertTrue(fileResource.exists());
      Assert.assertTrue(file.exists());
      Assert.assertEquals("Hello World", fileResource.getContents());
   }

   @Test(expected = ResourceTransactionException.class)
   public void testResourceTimeout()
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      transaction.setTransactionTimeout(-100);
   }

   @Test(expected = ResourceTransactionException.class)
   public void testResourceTimeoutInvalidatesTransaction() throws Exception
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      transaction.setTransactionTimeout(2);
      transaction.begin();
      Thread.sleep(3000);
      transaction.commit();
   }

   @Test
   public void testResourceTransactionSucceeds() throws Exception
   {
      ResourceTransaction transaction = resourceFactory.getTransaction();
      Assert.assertFalse(transaction.isStarted());
      transaction.begin();
      Assert.assertTrue(transaction.isStarted());
      Thread.sleep(1000);
      transaction.commit();
      Assert.assertFalse(transaction.isStarted());
   }

   /**
    * @param tempDir
    * @return
    * @throws IOException
    */
   private File createTempFile(File tempDir, boolean delete) throws IOException
   {
      File file = File.createTempFile("fileresourcetest", ".tmp", tempDir);
      if (delete)
      {
         file.delete();
      }
      return file;
   }
}
