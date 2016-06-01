/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_ENTITY_PACKAGE;
import static org.jboss.forge.addon.javaee.jpa.ui.LifecycleType.POST_LOAD;
import static org.jboss.forge.addon.javaee.jpa.ui.LifecycleType.POST_PERSIST;
import static org.jboss.forge.addon.javaee.jpa.ui.LifecycleType.PRE_REMOVE;
import static org.jboss.forge.addon.javaee.jpa.ui.LifecycleType.PRE_UPDATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
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
 * Tests the {@link JPANewEntityListenerCommand} behavior
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@RunWith(Arquillian.class)
public class JPANewEntityListenerCommandTest
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
      projectHelper.installJPA_2_0(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityListenerCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof JPANewEntityListenerCommand);
         assertTrue(controller.getCommand() instanceof AbstractJPACommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("JPA: New Entity Listener", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertEquals(6, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("lifecycles"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_ENTITY_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("jpa-new-entity-listener --named Dummy --lifecycles PRE_PERSIST", 10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(JPAFacet.class));
   }

   @Test
   public void testCreateEntityListener() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityListenerCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyListener");
         controller.setValueFor("lifecycles", Arrays.asList(POST_PERSIST));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + "." + DEFAULT_ENTITY_PACKAGE;
      JavaResource javaResource = facet.getJavaResource(packageName + ".MyListener");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> listener = javaResource.getJavaType();
      Assert.assertEquals(1, listener.getMethods().size());
      Assert.assertTrue(listener.getMethods().get(0).isReturnTypeVoid());
      Assert.assertEquals(1, listener.getMethods().get(0).getParameters().size());
      Assert.assertEquals("object", listener.getMethods().get(0).getParameters().get(0).getName());
      Assert.assertEquals("postPersist", listener.getMethods().get(0).getName());
      Assert.assertEquals(Visibility.PRIVATE, listener.getMethods().get(0).getVisibility());
   }

   @Test
   public void testCreateEntityListenerWithMultipleLifecycles() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityListenerCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyListener");
         controller.setValueFor("lifecycles", Arrays.asList(POST_PERSIST, PRE_REMOVE, POST_LOAD, PRE_UPDATE));
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      String packageName = project.getFacet(JavaSourceFacet.class).getBasePackage() + "." + DEFAULT_ENTITY_PACKAGE;
      JavaResource javaResource = facet.getJavaResource(packageName + ".MyListener");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> listener = javaResource.getJavaType();
      Assert.assertEquals(4, listener.getMethods().size());
   }
}
