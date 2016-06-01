/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.visit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
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
public class ResourceVisitorTest
{
   private ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testResourceVisit() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      File tempFile = createTempFile(tempDir, false);
      tempFile.deleteOnExit();
      FileResource<?> dirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      try
      {

         final AtomicInteger integer = new AtomicInteger(0);
         new ResourceVisit(dirResource).perform(new ResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, Resource<?> resource)
            {
               integer.incrementAndGet();
            }
         });

         Assert.assertEquals(2, integer.get());
      }
      finally
      {
         dirResource.delete(true);
      }
   }

   @Test
   public void testResourceVisitTerminate() throws IOException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      File tempFile = createTempFile(tempDir, false);
      tempFile.deleteOnExit();
      File tempFile2 = createTempFile(tempDir, false);
      tempFile2.deleteOnExit();
      FileResource<?> dirResource = resourceFactory.create(DirectoryResource.class, tempDir);
      try
      {

         final AtomicInteger integer = new AtomicInteger(0);
         new ResourceVisit(dirResource).perform(new ResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, Resource<?> resource)
            {
               integer.incrementAndGet();
               context.terminate();
            }
         });

         Assert.assertEquals(1, integer.get());
      }
      finally
      {
         dirResource.delete(true);
      }
   }

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
