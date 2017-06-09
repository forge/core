/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi;

import static org.hamcrest.CoreMatchers.*;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.ui.CDINewBeanCommand;
import org.jboss.forge.addon.javaee.cdi.ui.CDINewQualifierCommand;
import org.jboss.forge.addon.javaee.jpa.ui.JPANewEntityCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests to verify the behavior of the {@link CDIOperations} implementation class.
 */
@RunWith(Arquillian.class)
public class CDIOperationsTest
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
   private ProjectHelper projectHelper;

   private Project project;

   @Inject
   private CDIOperations cdiOperations;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installCDI_1_0(project);
      projectHelper.installJPA_2_0(project);
   }

   @Test
   public void testGetProjectInjectableBeans() throws Exception
   {
      Assert.assertEquals(0, cdiOperations.getProjectInjectableBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewQualifierCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyQualifier");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(0, cdiOperations.getProjectInjectableBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectInjectableBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEntity");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectInjectableBeans(project).size());
   }

   @Test
   public void testGetProjectInjectionPointBeans() throws Exception
   {
      Assert.assertEquals(0, cdiOperations.getProjectInjectionPointBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewQualifierCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyQualifier");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(0, cdiOperations.getProjectInjectableBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectInjectableBeans(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEntity");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectInjectableBeans(project).size());
   }

   @Test
   public void testGetProjectQualifiers() throws Exception
   {
      Assert.assertEquals(0, cdiOperations.getProjectQualifiers(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewQualifierCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyQualifier");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectQualifiers(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(CDINewBeanCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyBean");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectQualifiers(project).size());

      try (CommandController controller = uiTestHarness.createCommandController(JPANewEntityCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "MyEntity");
         controller.setValueFor("targetPackage", "org.jboss.forge.test");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
      }
      Assert.assertEquals(1, cdiOperations.getProjectQualifiers(project).size());
   }
}
