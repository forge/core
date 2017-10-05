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

import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
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
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.Parameter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for {@link CDIAddProducerMethodCommand}
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class CDIAddProducerMethodCommandTest
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
      projectHelper.installCDI_1_1(project);
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDIAddProducerMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDIAddProducerMethodCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: Add Producer Method", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(11, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetClass"));
         assertTrue(controller.hasInput("returnType"));
         assertTrue(controller.hasInput("qualifiers"));
         assertTrue(controller.hasInput("accessType"));
         assertTrue(controller.hasInput("scope"));
         assertTrue(controller.hasInput("qualifiers"));
         assertTrue(controller.hasInput("alternative"));
         assertTrue(controller.hasInput("defaultedName"));
         assertTrue(controller.hasInput("disposer"));
         assertTrue(controller.hasInput("injectionPoint"));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("cdi-new-bean --named DummyBean --target-package org.test", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute(
               "cdi-add-producer-method --named dummy --return-type java.lang.String --target-class org.test.DummyBean",
               10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testAddProducerMethod() throws Exception
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

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddProducerMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "produce");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyBean");
         controller.setValueFor("returnType", "java.lang.String");
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
      Assert.assertEquals(1, myBean.getMethods().size());
      Assert.assertEquals(0, myBean.getInterfaces().size());
      Method<?, ?> method = myBean.getMethods().get(0);
      Assert.assertEquals("produce", method.getName());
      Assert.assertEquals("java.lang.String", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.hasAnnotation(Produces.class));
   }

   @Test
   public void testAddProducerMethodWithDisposer() throws Exception
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

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddProducerMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "produce");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyBean");
         controller.setValueFor("returnType", "java.lang.String");
         controller.setValueFor("disposer", true);
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
      Assert.assertEquals(2, myBean.getMethods().size());
      Assert.assertEquals(0, myBean.getInterfaces().size());
      Method<?, ?> disposerMethod = myBean.getMethod("disposeString", String.class);
      Assert.assertNotNull(disposerMethod);
      Assert.assertEquals(1, disposerMethod.getParameters().size());
      Parameter<?> parameter = disposerMethod.getParameters().get(0);
      Assert.assertTrue(parameter.hasAnnotation(Disposes.class));
      Assert.assertEquals("java.lang.String", parameter.getType().getQualifiedName());
      Assert.assertEquals("stringToDispose", parameter.getName());
   }
}