/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_SERVLET_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@RunWith(Arquillian.class)
public class ServletNewServletCommandTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installServlet_3_1(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(ServletNewServletCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof ServletNewServletCommand);
         assertTrue(controller.getCommand() instanceof AbstractServletNewCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("Servlet: New Servlet", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("Servlet", metadata.getCategory().getSubCategory().getName());
         assertEquals(7, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("methods"));
         assertTrue(controller.hasInput("urlPatterns"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_SERVLET_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("servlet-new-servlet --named Dummy", 10, TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
   }

   @Test
   public void testCreateNewServlet() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(ServletNewServletCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServlet");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServlet");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(WebServlet.class));
      assertEquals(1,
               javaResource.getJavaType().getAnnotation(WebServlet.class).getStringArrayValue("urlPatterns").length);
      assertEquals("myServlet",
               javaResource.getJavaType().getAnnotation(WebServlet.class).getStringArrayValue("urlPatterns")[0]);
   }

   @Test
   public void testCreateNewServletWithURLPatterns() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(ServletNewServletCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServlet");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("urlPatterns", Arrays.asList("pattern1", "pattern2"));
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServlet");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(WebServlet.class));
      assertEquals(2,
               javaResource.getJavaType().getAnnotation(WebServlet.class).getStringArrayValue("urlPatterns").length);
   }

   @Test
   public void testCreateNewServletWithMethods() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(ServletNewServletCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServlet");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("methods", Arrays.asList(ServletMethod.GET, ServletMethod.POST));
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServlet");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(WebServlet.class));
      JavaClassSource javaClass = javaResource.getJavaType();
      assertEquals(2, javaClass.getMethods().size());
   }
}