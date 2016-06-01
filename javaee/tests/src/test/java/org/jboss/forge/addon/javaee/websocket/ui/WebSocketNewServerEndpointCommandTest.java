/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_WEBSOCKET_PACKAGE;
import static org.jboss.forge.addon.javaee.websocket.ui.WebSocketMethodType.ON_CLOSE;
import static org.jboss.forge.addon.javaee.websocket.ui.WebSocketMethodType.ON_ERROR;
import static org.jboss.forge.addon.javaee.websocket.ui.WebSocketMethodType.ON_MESSAGE;
import static org.jboss.forge.addon.javaee.websocket.ui.WebSocketMethodType.ON_OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.websocket.WebSocketFacet;
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
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link WebSocketNewServerEndpointCommand} behavior
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@RunWith(Arquillian.class)
public class WebSocketNewServerEndpointCommandTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installWebSocket_1_1(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(WebSocketNewServerEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof WebSocketNewServerEndpointCommand);
         assertTrue(controller.getCommand() instanceof AbstractWebsocketCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("WebSocket: New Server Endpoint", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("WebSocket", metadata.getCategory().getSubCategory().getName());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertEquals(7, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("methods"));
         assertTrue(controller.hasInput("uri"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_WEBSOCKET_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("websocket-new-server-endpoint --named Dummy", 10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(WebSocketFacet.class));
   }

   @Test
   public void testCreateWebSocketServerEndpoint() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(WebSocketNewServerEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyWebSocketServerEndpoint");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + "." + DEFAULT_WEBSOCKET_PACKAGE;
      JavaResource javaResource = facet.getJavaResource(packageName + ".MyWebSocketServerEndpoint");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(ServerEndpoint.class));
      assertEquals("/myWebSocketServer",
               javaResource.getJavaType().getAnnotation(ServerEndpoint.class).getStringValue());
      JavaClass<?> javaType = javaResource.getJavaType();
      Assert.assertEquals(0, javaType.getMethods().size());
      Assert.assertEquals(0, javaType.getFields().size());
   }

   @Test
   public void testCreateWebSocketServerEndpointWithUriAndMethod() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(WebSocketNewServerEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyWebSocketServerEndpointWithURI");
         controller.setValueFor("uri", "myUri");
         controller.setValueFor("methods", Arrays.asList(ON_MESSAGE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + "." + DEFAULT_WEBSOCKET_PACKAGE;
      JavaResource javaResource = facet.getJavaResource(packageName + ".MyWebSocketServerEndpointWithURI");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(ServerEndpoint.class));
      assertEquals("/myUri",
               javaResource.getJavaType().getAnnotation(ServerEndpoint.class).getStringValue());
      JavaClass<?> javaType = javaResource.getJavaType();
      Assert.assertEquals(1, javaType.getMethods().size());
      Assert.assertEquals(0, javaType.getFields().size());
      Assert.assertEquals(2, javaType.getMethods().get(0).getParameters().size());
      Assert.assertEquals(Visibility.PUBLIC, javaType.getMethods().get(0).getVisibility());

   }

   @Test
   public void testCreateWebSocketServerEndpointWithMultipleMethods() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(WebSocketNewServerEndpointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyWebSocketServerEndpointWithMethods");
         controller.setValueFor("methods", Arrays.asList(ON_MESSAGE, ON_CLOSE, ON_ERROR, ON_OPEN));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + "." + DEFAULT_WEBSOCKET_PACKAGE;
      JavaResource javaResource = facet.getJavaResource(packageName + ".MyWebSocketServerEndpointWithMethods");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> listener = javaResource.getJavaType();
      Assert.assertEquals(4, listener.getMethods().size());
   }
}
