/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_CDI_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@RunWith(Arquillian.class)
public class CDINewInterceptorCommandTest
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
      try (CommandController controller = uiTestHarness.createCommandController(CDINewInterceptorCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDINewInterceptorCommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: New Interceptor", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(7, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("interceptorBinding"));
         assertTrue(controller.hasInput("enabled"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertFalse(controller.hasInput("priority"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_CDI_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      shellTest.execute("cdi-new-interceptor-binding --named DummyInterceptorBinding --target-package org.test", 10,
               TimeUnit.SECONDS);
      Result result = shellTest.execute(
               "cdi-new-interceptor --named Dummy --interceptor-binding org.test.DummyInterceptorBinding", 10,
               TimeUnit.SECONDS);

      Assert.assertThat(result, not(instanceOf(Failed.class)));
      Assert.assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testCreateNewInterceptor() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewInterceptorCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyInterceptor");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("interceptorBinding", "javax.inject.Named");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyInterceptor");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> interceptor = javaResource.getJavaType();
      Assert.assertTrue(interceptor.hasAnnotation(Named.class));
      Assert.assertTrue(interceptor.hasAnnotation(Interceptor.class));
      Assert.assertTrue(interceptor.getMethods().get(0).hasAnnotation(AroundInvoke.class));
      Assert.assertFalse(interceptor.hasAnnotation(Inherited.class));
      CDIFacet_1_0 cdiFacet = project.getFacet(CDIFacet_1_0.class);
      List<String> allClazz = cdiFacet.getConfig().getOrCreateInterceptors().getAllClazz();
      Assert.assertThat(allClazz.size(), is(0));
   }

   @Test
   public void testCreateNewEnabledInterceptor() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewInterceptorCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyInterceptor");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("interceptorBinding", "javax.inject.Named");
         controller.setValueFor("enabled", true);
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyInterceptor");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> interceptor = javaResource.getJavaType();
      Assert.assertTrue(interceptor.hasAnnotation(Named.class));
      Assert.assertTrue(interceptor.hasAnnotation(Interceptor.class));
      Assert.assertTrue(interceptor.getMethods().get(0).hasAnnotation(AroundInvoke.class));
      Assert.assertFalse(interceptor.hasAnnotation(Inherited.class));
      CDIFacet_1_0 cdiFacet = project.getFacet(CDIFacet_1_0.class);
      List<String> allClazz = cdiFacet.getConfig().getOrCreateInterceptors().getAllClazz();
      Assert.assertThat(allClazz.size(), is(1));
      Assert.assertThat(allClazz.get(0), equalTo("org.jboss.forge.test.MyInterceptor"));
   }

   @Test
   public void testCreateNewInterceptorUsingPackageWildcard() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewInterceptorBindingCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyInterceptorBinding");
         controller.setValueFor("targetPackage", "~.interceptors");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      try (CommandController controller = uiTestHarness.createCommandController(CDINewInterceptorCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyInterceptor");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("interceptorBinding", "~.interceptors.MyInterceptorBinding");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyInterceptor");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      JavaClass<?> interceptor = javaResource.getJavaType();
      Assert.assertTrue(interceptor.hasAnnotation(facet.getBasePackage() + ".interceptors.MyInterceptorBinding"));
      Assert.assertTrue(interceptor.hasAnnotation(Interceptor.class));
      Assert.assertTrue(interceptor.getMethods().get(0).hasAnnotation(AroundInvoke.class));
      Assert.assertFalse(interceptor.hasAnnotation(Inherited.class));
   }

}