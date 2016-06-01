/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.ejb.ui.NewEJBCommand;
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
public class CDIAddInjectionPointCommandTest
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
      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDIAddInjectionPointCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: Add Injection Point", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(4, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetClass"));
         assertTrue(controller.hasInput("type"));
         assertTrue(controller.hasInput("qualifiers"));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("cdi-new-bean --named DummyBean --target-package org.test", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute(
               "cdi-add-injection-point --named dummy --type java.lang.String --target-class org.test.DummyBean", 10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testCreateNewInjectionPointOnBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test.bean");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyBean");
         controller.setValueFor("type", "java.lang.String");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.bean.MyBean");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myBean = javaResource.getJavaType();
      Assert.assertEquals(0, myBean.getMethods().size());
      Assert.assertEquals(0, myBean.getInterfaces().size());
      Assert.assertEquals(1, myBean.getMembers().size());
      Assert.assertEquals(1, myBean.getProperties().size());
      Assert.assertEquals(1, myBean.getFields().size());
      Assert.assertEquals("ip", myBean.getFields().get(0).getName());
      Assert.assertEquals("java.lang.String", myBean.getFields().get(0).getType().getQualifiedName());
   }

   @Test
   public void testCreateNewInjectionPointOnEJB() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(NewEJBCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEJB");
         controller.setValueFor("targetPackage", "org.jboss.forge.test.ejb");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip");
         controller.setValueFor("targetClass", "org.jboss.forge.test.ejb.MyEJB");
         controller.setValueFor("type", "java.lang.String");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.ejb.MyEJB");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myEJB = javaResource.getJavaType();
      Assert.assertEquals(0, myEJB.getMethods().size());
      Assert.assertEquals(1, myEJB.getProperties().size());
   }

   @Test
   public void testCreateNewInjectionPointOfBeanType() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBeanType");
         controller.setValueFor("targetPackage", "org.jboss.forge.test.type");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "TheBeanToBeInjected");
         controller.setValueFor("targetPackage", "org.jboss.forge.test.type");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip");
         controller.setValueFor("targetClass", "org.jboss.forge.test.type.MyBeanType");
         controller.setValueFor("type", "org.jboss.forge.test.type.TheBeanToBeInjected");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.type.MyBeanType");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myBean = javaResource.getJavaType();
      Assert.assertEquals(1, myBean.getFields().size());
      Assert.assertEquals("ip", myBean.getFields().get(0).getName());
      Assert.assertEquals("org.jboss.forge.test.type.TheBeanToBeInjected", myBean.getFields().get(0).getType()
               .getQualifiedName());
   }

   @Test
   public void testCreateNewInjectionPointWithQualifiers() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBeanWithQualifiers");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip");
         controller.setValueFor("targetClass", "org.jboss.forge.test.MyBeanWithQualifiers");
         controller.setValueFor("type", "java.lang.String");
         controller.setValueFor("qualifiers", Arrays.asList("java.lang.annotation.Documented", "java.lang.Override"));

         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyBeanWithQualifiers");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myBean = javaResource.getJavaType();
      Assert.assertEquals(1, myBean.getFields().size());
      Assert.assertEquals("ip", myBean.getFields().get(0).getName());
      Assert.assertEquals(3, myBean.getFields().get(0).getAnnotations().size());
   }

   @Test
   public void testCreateTwoInjectionPoints() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBeanWithTwoIP");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip");
         controller.setValueFor("targetClass", "org.jboss.forge.test.MyBeanWithTwoIP");
         controller.setValueFor("type", "java.lang.String");

         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddInjectionPointCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "ip2");
         controller.setValueFor("targetClass", "org.jboss.forge.test.MyBeanWithTwoIP");
         controller.setValueFor("type", "java.lang.String");

         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyBeanWithTwoIP");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myBean = javaResource.getJavaType();
      Assert.assertEquals(0, myBean.getMethods().size());
      Assert.assertEquals(0, myBean.getInterfaces().size());
      Assert.assertEquals(2, myBean.getMembers().size());
      Assert.assertEquals(2, myBean.getProperties().size());
      Assert.assertEquals(2, myBean.getFields().size());
   }
}