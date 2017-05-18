/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import static org.hamcrest.CoreMatchers.*;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_REST_PACKAGE;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
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
public class RestNewEndpointCommandTest
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
      projectHelper.installJAXRS_2_0(project, RestConfigurationStrategyFactory.createUsingWebXml("/rest"));
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(RestNewEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof RestNewEndpointCommand);
         assertTrue(controller.getCommand() instanceof AbstractRestNewCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("REST: New Endpoint", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JAX-RS", metadata.getCategory().getSubCategory().getName());
         assertEquals(7, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("methods"));
         assertTrue(controller.hasInput("path"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_REST_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("rest-new-endpoint --named Dummy", 10, TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(RestFacet.class));
   }

   @Test
   public void testCreateNewRestEndpointCalledEndpoint() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(RestNewEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEndpoint");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyEndpoint");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(Path.class));
      assertEquals("/my", javaResource.getJavaType().getAnnotation(Path.class).getStringValue());
   }

   @Test
   public void testCreateNewRestEndpoint() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(RestNewEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyService");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyService");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(Path.class));
      assertEquals("/myService", javaResource.getJavaType().getAnnotation(Path.class).getStringValue());
   }

   @Test
   public void testCreateNewServletWithPath() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(RestNewEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEndpoint");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("path", "myPath");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyEndpoint");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(Path.class));
      assertEquals("/myPath", javaResource.getJavaType().getAnnotation(Path.class).getStringValue());
   }

   @Test
   public void testCreateNewServletWithMethods() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(RestNewEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEndpoint");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("methods", Arrays.asList(RestMethod.GET, RestMethod.POST));
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyEndpoint");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertEquals(0, javaResource.getJavaType().getSyntaxErrors().size());
      assertTrue(javaResource.getJavaType().hasAnnotation(Path.class));
      JavaClass<?> javaClass = javaResource.getJavaType();
      assertEquals(2, javaClass.getMethods().size());
   }
}