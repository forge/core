/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.Startup;
import org.jboss.forge.shell.integration.BufferManager;
import org.jboss.seam.render.RenderRoot;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.solder.SolderRoot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public abstract class AbstractShellTest
{
   static
   {
      System.setProperty("forge.debug.no_auto_init_streams", "true");
   }

   @Deployment
   public static JavaArchive getDeployment()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addPackages(true, RenderRoot.class.getPackage()).addPackages(true, SolderRoot.class.getPackage())
               .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private Shell shell;

   @Inject
   private BeanManager beanManager;

   private Queue<String> inputQueue = new LinkedList<String>();

   private ByteArrayOutputStream output = new ByteArrayOutputStream();

   private final List<FileResource<?>> tempFolders = new ArrayList<FileResource<?>>();

   private static final String PKG = AbstractShellTest.class.getSimpleName().toLowerCase();

   @Inject
   private Instance<Project> project;

   @Inject
   private ResourceFactory factory;

   @BeforeClass
   public static void before() throws IOException
   {
   }

   @AfterClass
   public static void after()
   {
   }

   @Before
   public void beforeTest() throws Exception
   {
      shell.setCurrentResource(createTempFolder());
      beanManager.fireEvent(new Startup());
      beanManager.fireEvent(new PostStartup());
      shell.setVerbose(true);
      shell.setExceptionHandlingEnabled(false);

      resetInputQueue();
      shell.setOutputStream(output);
      shell.setAnsiSupported(false);
   }

   protected DirectoryResource createTempFolder() throws IOException
   {
      File tempFolder = File.createTempFile(PKG, null);
      tempFolder.delete();
      tempFolder.mkdirs();
      DirectoryResource resource = factory.getResourceFrom(tempFolder).reify(DirectoryResource.class);
      tempFolders.add(resource);
      return resource;
   }

   /**
    * Reset the shell input queue (called automatically before each test.)
    */
   protected void resetInputQueue() throws IOException
   {
      inputQueue = new LinkedList<String>();
      QueuedInputStream is = new QueuedInputStream(inputQueue);

      shell.registerBufferManager(new BufferManager()
      {
         @Override
         public void bufferOnlyMode()
         {

         }

         @Override
         public void directWriteMode()
         {
         }

         @Override
         public void flushBuffer()
         {
         }

         @Override
         public void write(final int b)
         {
         }

         @Override
         public void write(final byte b)
         {
         }

         @Override
         public void write(final byte[] b)
         {
         }

         @Override
         public void write(final byte[] b, final int offset, final int length)
         {
         }

         @Override
         public void write(final String s)
         {
         }

         @Override
         public void directWrite(final String s)
         {
         }

         @Override
         public void setBufferPosition(final int row, final int col)
         {
         }

         @Override
         public int getHeight()
         {
            return 0;
         }

         @Override
         public int getWidth()
         {
            return 0;
         }
      });

      shell.setInputStream(is);
   }

   @After
   public void afterTest() throws IOException
   {
      output.reset();
      for (FileResource<?> file : tempFolders)
      {
         if (file.exists())
         {
            Assert.assertTrue(file.delete(true));
         }
      }
   }

   protected void queueInputLines(final String... inputs)
   {
      for (String input : inputs)
      {
         if ((input == null) || "\0".equals(input))
         {
            inputQueue.add(input);
         }
         else
            inputQueue.add(input + "\n");
      }
   }

   protected BeanManager getBeanManager()
   {
      return beanManager;
   }

   protected Shell getShell()
   {
      return shell;
   }

   protected Project getProject()
   {
      return project.get();
   }

   public String getOutput()
   {
      return output.toString();
   }

   protected Project initializeProject(final PackagingType type) throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type " + type.toString());
      return getProject();
   }

   protected Project initializeJavaProject() throws Exception
   {
      return initializeProject(PackagingType.JAR);
   }

}
