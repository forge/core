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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
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
 * Test class for {@link CDIAddObserverMethodCommand}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CDIAddObserverMethodCommandTest
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
      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDIAddObserverMethodCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: Add Observer Method", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(6, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetClass"));
         assertTrue(controller.hasInput("eventType"));
         assertTrue(controller.hasInput("qualifiers"));
         assertTrue(controller.hasInput("accessType"));
         assertTrue(controller.hasInput("containerLifecyleEventType"));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("cdi-new-bean --named DummyBean --target-package org.test", 10, TimeUnit.SECONDS);
      Result result = shellTest.execute(
               "cdi-add-observer-method --named dummy --event-type java.lang.String --target-class org.test.DummyBean",
               10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testCreateNewObserverMethod() throws Exception
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

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "observe");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyBean");
         controller.setValueFor("eventType", "java.lang.String");
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
      Assert.assertEquals("observe", method.getName());
      Assert.assertEquals(1, method.getParameters().size());
      Assert.assertTrue(method.isPackagePrivate());
      Parameter<?> parameter = method.getParameters().get(0);
      Assert.assertTrue(parameter.hasAnnotation(Observes.class));
      Assert.assertEquals("java.lang.String", parameter.getType().getQualifiedName());
      Assert.assertEquals("event", parameter.getName());
   }

   @Test
   public void testCreateNewObserverMethodWithQualifiers() throws Exception
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

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "observe");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyBean");
         controller.setValueFor("eventType", "java.lang.String");
         controller.setValueFor("qualifiers",
                  Arrays.asList("javax.enterprise.inject.Default", "javax.enterprise.inject.Any"));
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
      Assert.assertEquals("observe", method.getName());
      Assert.assertEquals(1, method.getParameters().size());
      Parameter<?> parameter = method.getParameters().get(0);
      Assert.assertTrue(parameter.hasAnnotation(Observes.class));
      Assert.assertEquals("java.lang.String", parameter.getType().getQualifiedName());
      Assert.assertEquals("event", parameter.getName());
      Assert.assertEquals(3, parameter.getAnnotations().size());
      Assert.assertTrue(parameter.hasAnnotation(Observes.class));
      Assert.assertTrue(parameter.hasAnnotation(Default.class));
      Assert.assertTrue(parameter.hasAnnotation(Any.class));
   }

   @Test
   public void testCreateNewObserverMethodExtension() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewExtensionCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyExtension");
         controller.setValueFor("targetPackage", "org.jboss.forge.test.bean");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "pat");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyExtension");
         controller.setValueFor("containerLifecyleEventType", "ProcessAnnotatedType");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "adv");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyExtension");
         controller.setValueFor("containerLifecyleEventType", "AfterDeploymentValidation");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      // Test duplicate method
      try (CommandController controller = uiTestHarness.createCommandController(CDIAddObserverMethodCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "adv");
         controller.setValueFor("targetClass", "org.jboss.forge.test.bean.MyExtension");
         controller.setValueFor("containerLifecyleEventType", "AfterDeploymentValidation");
         assertFalse(controller.isValid());
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.bean.MyExtension");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> myExtension = javaResource.getJavaType();
      Assert.assertEquals(2, myExtension.getMethods().size());
      Assert.assertEquals(1, myExtension.getInterfaces().size());

      Method<?, ?> pat = myExtension.getMethod("pat", ProcessAnnotatedType.class);
      Assert.assertNotNull(pat);
      Assert.assertEquals(1, pat.getParameters().size());
      Assert.assertTrue(pat.isPackagePrivate());
      Parameter<?> patParam = pat.getParameters().get(0);
      Assert.assertTrue(patParam.hasAnnotation(Observes.class));
      Assert.assertEquals("javax.enterprise.inject.spi.ProcessAnnotatedType<?>", patParam.getType().getQualifiedNameWithGenerics());
      Assert.assertEquals("event", patParam.getName());

      Method<?, ?> adv = myExtension.getMethod("adv", AfterDeploymentValidation.class);
      Assert.assertNotNull(adv);
      Assert.assertEquals(1, adv.getParameters().size());
      Assert.assertTrue(adv.isPackagePrivate());
      Parameter<?> advParam = adv.getParameters().get(0);
      Assert.assertTrue(advParam.hasAnnotation(Observes.class));
      Assert.assertEquals("javax.enterprise.inject.spi.AfterDeploymentValidation", advParam.getType().getQualifiedName());
      Assert.assertEquals("event", advParam.getName());
   }
}