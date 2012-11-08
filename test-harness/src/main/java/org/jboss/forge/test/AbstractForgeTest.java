/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.container.ContainerControl;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public abstract class AbstractForgeTest
{
   public static JavaArchive getDeployment()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage());

      try
      {
         archive.addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
      }
      catch (Exception e)
      {
         System.err.println("No beans.xml file in deployment for current test.");
      }

      return archive;
   }

   @Inject
   private ContainerControl control;

   private ByteArrayOutputStream output = new ByteArrayOutputStream();

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
      control.start();
   }

   @After
   public void afterTest() throws IOException
   {
      control.stop();
   }

   protected String getOutput()
   {
      return output.toString();
   }

   public static Collection<GenericArchive> resolveDependencies(final String coords)
   {
      return DependencyResolvers.use(MavenDependencyResolver.class)
               .loadMetadataFromPom("pom.xml")
               .artifacts(coords)
               .resolveAs(GenericArchive.class);
   }

}
