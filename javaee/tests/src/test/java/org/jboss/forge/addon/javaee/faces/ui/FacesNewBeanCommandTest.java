/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.ui.BeanScope;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@RunWith(Arquillian.class)
public class FacesNewBeanCommandTest
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
      projectHelper.installFaces_2_2(project);
      projectHelper.installCDI_1_0(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(FacesNewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof FacesNewBeanCommand);
         assertTrue(controller.getCommand() instanceof AbstractFacesCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("Faces: New Bean", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JSF", metadata.getCategory().getSubCategory().getName());
         assertEquals(6, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("scoped"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_FACES_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("faces-new-bean --named Dummy", 10, TimeUnit.SECONDS);

      assertThat(result, not(instanceOf(Failed.class)));
      assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testCreateNewBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(FacesNewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyFacesBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyFacesBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertEquals(0, ((JavaClass<?>) javaResource.getJavaType()).getFields().size());
      assertEquals(0, ((JavaClass<?>) javaResource.getJavaType()).getMethods().size());
   }

   @Test
   public void testCreateNewBeanRequestScoped() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(FacesNewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyFacesBeanRequest");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.REQUEST.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyFacesBeanRequest");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertEquals(0, ((JavaClass<?>) javaResource.getJavaType()).getFields().size());
      assertEquals(0, ((JavaClass<?>) javaResource.getJavaType()).getMethods().size());
   }

   @Test
   public void testCreateNewBeanSessionScoped() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(FacesNewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyFacesBeanSession");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.SESSION.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyFacesBeanSession");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertEquals(1, ((JavaClass<?>) javaResource.getJavaType()).getFields().size());
      assertEquals(0, ((JavaClass<?>) javaResource.getJavaType()).getMethods().size());
   }

   @Test
   public void testCreateNewBeanConversationScoped() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(FacesNewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyFacesBeanConversation");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.CONVERSATION.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyFacesBeanConversation");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertEquals(2, ((JavaClass<?>) javaResource.getJavaType()).getFields().size());
      assertEquals(2, ((JavaClass<?>) javaResource.getJavaType()).getMethods().size());
   }
}