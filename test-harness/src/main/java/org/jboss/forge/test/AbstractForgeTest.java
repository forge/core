/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.forge.container.events.Shutdown;
import org.jboss.forge.container.events.Startup;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
   static
   {
      System.setProperty("forge.debug.no_auto_init_streams", "true");
   }

   @Deployment
   public static JavaArchive getDeployment()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private BeanManager beanManager;

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
      beanManager.fireEvent(new Startup());
   }

   @After
   public void afterTest() throws IOException
   {
      beanManager.fireEvent(new Shutdown());
   }

   protected BeanManager getBeanManager()
   {
      return beanManager;
   }

   protected String getOutput()
   {
      return output.toString();
   }

}
