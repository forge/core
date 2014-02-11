/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Transient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.ui.NewFieldWizard;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class NewFieldWizardTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven")
               );
   }

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private UITestHarness uiTestHarness;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
   }

   @Test
   public void testNewField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRootDirectory()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "firstName");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field firstName created", result.getMessage());
      }
      JavaClass javaClass = (JavaClass) entity.getJavaSource();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<JavaClass> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType());
   }

   @Test
   public void testNewTransientField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRootDirectory()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "firstName");
         controller.setValueFor("transient", Boolean.TRUE);
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Transient Field firstName created", result.getMessage());
      }
      JavaClass javaClass = (JavaClass) entity.getJavaSource();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<JavaClass> field = javaClass.getField("firstName");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(Transient.class));
      Assert.assertEquals("String", field.getType());
   }
}
