/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenFacetsTest;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaExecutionFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.Startup;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Ignore
public class JavaExecutionFacetTest
{
   private JavaExecutionFacet executionFacet;

   @Inject
   Shell shell;

   private static final String PKG = MavenFacetsTest.class.getSimpleName().toLowerCase();
   private static File tempFolder;

   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addPackages(true, Root.class.getPackage())
               .addClass(ResourceFactory.class)
               .addAsManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"))
               .addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension");
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   protected static Project project;

   @Inject
   BeanManager beanManager;

   @Before
   @SuppressWarnings("unchecked")
   public void postConstruct() throws IOException
   {
      if (project == null)
      {
         tempFolder = File.createTempFile(PKG, null);
         tempFolder.delete();
         tempFolder.mkdirs();
         File classesDir = new File(tempFolder.getAbsolutePath() + "/src/test/java");
         classesDir.mkdirs();

         InputStream resourceAsStream = this.getClass().getResourceAsStream("/test/TestClass.java");
         FileOutputStream out = new FileOutputStream(classesDir + "/TestClass.java");
         int b;
         while ((b = resourceAsStream.read()) != -1)
         {
            out.write(b);
         }

         out.close();
         resourceAsStream.close();
         project = projectFactory.createProject(
                  ResourceUtil.getContextDirectory(resourceFactory.getResourceFrom(tempFolder)),
                  MavenCoreFacet.class, JavaSourceFacet.class, ResourceFacet.class, WebResourceFacet.class,
                  DependencyFacet.class, PackagingFacet.class, JavaExecutionFacet.class);

         beanManager.fireEvent(new Startup());
         beanManager.fireEvent(new PostStartup());
      }

      executionFacet = project.getFacet(JavaExecutionFacet.class);
   }

   @Test
   public void testExecuteProjectClass() throws Exception
   {
      File f = new File(tempFolder + "/test.txt");
      Assume.assumeThat(f.exists(), is(false));
      executionFacet.executeProjectClass("test.TestClass");
      assertThat(f.exists(), is(true));
   }

   @Test
   public void testExecuteProjectClassWithArguments() throws Exception
   {
      executionFacet.executeProjectClass("test.TestClass", "a", "b", "c");
      File f = new File(tempFolder + "/test.txt");
      Scanner scanner = new Scanner(f);
      String s = scanner.nextLine();
      assertThat(s, is("a b c"));
      scanner.close();
   }

   @After
   public void removeTestFile()
   {
      File f = new File(tempFolder + "/test.txt");
      if (f.exists())
      {
         f.delete();
      }
   }
}
