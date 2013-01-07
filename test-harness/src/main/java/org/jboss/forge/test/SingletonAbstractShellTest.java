/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test;

import static org.junit.Assert.assertTrue;

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
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.Startup;
import org.jboss.seam.render.RenderRoot;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.solder.SolderRoot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public abstract class SingletonAbstractShellTest
{
   static
   {
      System.setProperty("forge.debug.no_auto_init_streams", "true");
      System.setProperty("forge.analytics.no_prompt", "true");
   }

   @Deployment
   public static JavaArchive getDeployment()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addPackages(true, Root.class.getPackage())
               .addPackages(true, RenderRoot.class.getPackage())
               .addPackages(true, SolderRoot.class.getPackage())
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private BeanManager beanManager;
   @Inject
   private Shell shell;
   @Inject
   private ResourceFactory factory;

   private DirectoryResource tempFolder;
   private static final List<FileResource<?>> tempFolders = new ArrayList<FileResource<?>>();
   private static final String PKG = SingletonAbstractShellTest.class.getSimpleName().toLowerCase();
   private static Queue<String> inputQueue = new LinkedList<String>();

   private ByteArrayOutputStream output = new ByteArrayOutputStream();

   @Inject
   private Instance<Project> project;

   @Before
   public void beforeTest() throws Exception
   {
      if (shell == null)
      {
         throw new IllegalStateException("Failed to initialize Shell instance for test.");
      }

      if (tempFolder == null)
      {
         shell.setOutputStream(output);
         shell.setCurrentResource(createTempFolder());
         beanManager.fireEvent(new Startup());
         beanManager.fireEvent(new PostStartup());
         shell.setVerbose(true);
         shell.setExceptionHandlingEnabled(false);
         shell.setAnsiSupported(false);

         resetInputQueue();
      }
   }

   @After
   public void afterTest() throws IOException
   {
      resetInputQueue();
   }

   @AfterClass
   public static void afterClass() throws IOException
   {
      for (FileResource<?> file : tempFolders)
      {
         if (file.exists())
         {
            assertTrue(file.delete(true));
         }
      }
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
      shell.setInputStream(is);
   }

   protected void queueInputLines(final String... inputs)
   {
      for (String input : inputs)
      {
         inputQueue.add(input + "\n");
      }
   }

   protected BeanManager getBeanManager()
   {
      return beanManager;
   }

   protected String getOutput()
   {
      return output.toString();
   }

   protected Shell getShell()
   {
      return shell;
   }

   protected Project getProject()
   {
      return project.get();
   }

   protected Project initializeJavaProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test");

      Project project = getProject();
      tempFolder = project.getProjectRoot();
      tempFolders.add(tempFolder);
      return project;
   }

}
