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
import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_CDI_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

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
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CDINewBeanCommandTest
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
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof CDINewBeanCommand);
         assertTrue(controller.getCommand() instanceof AbstractCDICommand);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("CDI: New Bean", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("CDI", metadata.getCategory().getSubCategory().getName());
         assertEquals(11, controller.getInputs().size());
         assertFalse("Project is created, shouldn't have targetLocation", controller.hasInput("targetLocation"));
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetPackage"));
         assertTrue(controller.hasInput("overwrite"));
         assertTrue(controller.hasInput("scoped"));
         assertTrue(controller.hasInput("customScopeAnnotation"));
         assertTrue(controller.hasInput("qualifier"));
         assertTrue(controller.hasInput("alternative"));
         assertTrue(controller.hasInput("withNamed"));
         assertTrue(controller.hasInput("extends"));
         assertTrue(controller.hasInput("implements"));
         assertTrue(controller.hasInput("enabled"));
         assertTrue(controller.getValueFor("targetPackage").toString().endsWith(DEFAULT_CDI_PACKAGE));
      }
   }

   @Test
   public void checkCommandShell() throws Exception
   {
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute("cdi-new-bean --named Dummy", 10, TimeUnit.SECONDS);

      assertThat(result, not(instanceOf(Failed.class)));
      assertTrue(project.hasFacet(CDIFacet.class));
   }

   @Test
   public void testCreateNewBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.SESSION.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewRequestScopedBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyRequestScopedBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.REQUEST.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyRequestScopedBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(RequestScoped.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewConversationScopedBean() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyConversationScopedBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.CONVERSATION.name());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyConversationScopedBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(ConversationScoped.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewBeanWithAlternativeAndNamed() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.SESSION.name());
         controller.setValueFor("alternative", true);
         controller.setValueFor("withNamed", true);
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertTrue(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertTrue(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertTrue(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewBeanWithNoAlternativeAndNamed() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("alternative", false);
         controller.setValueFor("withNamed", false);
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewBeanWithQualifier() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("qualifier", "javax.inject.Named");
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertFalse(javaResource.getJavaType().hasAnnotation(SessionScoped.class));
      assertTrue(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }

   @Test
   public void testCreateNewBeanCustomScope() throws Exception
   {
      JavaAnnotationSource ann = Roaster.create(JavaAnnotationSource.class).setName("MyCustomScope")
               .setPackage("org.jboss.forge.test.scope");
      ann.addAnnotation(NormalScope.class);
      project.getFacet(JavaSourceFacet.class).saveJavaSource(ann);
      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyCustomServiceBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         controller.setValueFor("scoped", BeanScope.CUSTOM.name());
         assertFalse(controller.isValid());
         controller.setValueFor("customScopeAnnotation", ann.getQualifiedName());
         assertTrue(controller.isValid());
         assertTrue(controller.canExecute());
         Result result = controller.execute();
         assertThat(result, is(not(instanceOf(Failed.class))));
      }

      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource("org.jboss.forge.test.MyCustomServiceBean");
      assertNotNull(javaResource);
      assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      assertFalse(javaResource.getJavaType().hasAnnotation(Named.class));
      assertFalse(javaResource.getJavaType().hasAnnotation(Alternative.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasInterface(Serializable.class));
      assertFalse(((JavaClass<?>) javaResource.getJavaType()).hasField("serialVersionUID"));
   }
}