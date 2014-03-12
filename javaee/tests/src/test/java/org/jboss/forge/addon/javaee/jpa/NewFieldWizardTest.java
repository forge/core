/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.ui.NewFieldWizard;
import org.jboss.forge.addon.javaee.jpa.ui.RelationshipType;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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

   @Inject
   private FieldOperations fieldOperations;

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
         controller.setValueFor("columnName", "FIRST_NAME_COLUMN");
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
      Assert.assertEquals("FIRST_NAME_COLUMN", field.getAnnotation(Column.class).getStringValue("name"));
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

   @Test
   public void testUpdateExistingField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaSource<?> javaSource = entity.getJavaSource();
      fieldOperations.addFieldTo((JavaClass) javaSource, "String", "firstName");
      entity.setContents(javaSource.toString());
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRootDirectory()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "firstName");
         controller.setValueFor("length", "100");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field firstName updated", result.getMessage());
      }
      JavaClass javaClass = (JavaClass) entity.getJavaSource();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<JavaClass> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType());
      Assert.assertEquals("100", field.getAnnotation(Column.class).getStringValue("length"));
   }

   @Test
   public void testNewOneToManyField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource otherEntity = projectHelper.createJPAEntity(project, "Account");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRootDirectory()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "accounts");
         controller.setValueFor("type", otherEntity.getJavaSource().getCanonicalName());
         controller.setValueFor("relationshipType", RelationshipType.ONE_TO_MANY);
         Assert.assertTrue(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         assertThat("Result should be of type CompositeResult", result instanceof CompositeResult, equalTo(true));
         CompositeResult compositeResult = (CompositeResult) result;
         Assert.assertEquals("Relationship One-to-Many created", compositeResult.getResults().get(1).getMessage());
      }
      JavaClass javaClass = (JavaClass) entity.getJavaSource();
      Assert.assertTrue(javaClass.hasField("accounts"));
      final Field<JavaClass> field = javaClass.getField("accounts");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(OneToMany.class));
      Assert.assertEquals("Set", field.getType());
   }

   @Test
   public void testNewOneToManyEagerFetchField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource otherEntity = projectHelper.createJPAEntity(project, "Account");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRootDirectory()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "accounts");
         controller.setValueFor("type", otherEntity.getJavaSource().getCanonicalName());
         controller.setValueFor("relationshipType", RelationshipType.ONE_TO_MANY);
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next();
         controller.setValueFor("fetchType", FetchType.EAGER);
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         assertThat ("Result should be of type CompositeResult", result instanceof CompositeResult, equalTo(true));
         CompositeResult compositeResult = (CompositeResult) result;
         Assert.assertEquals("Relationship One-to-Many created", compositeResult.getResults().get(1).getMessage());
      }
      JavaClass javaClass = (JavaClass) entity.getJavaSource();
      Assert.assertTrue(javaClass.hasField("accounts"));
      final Field<JavaClass> field = javaClass.getField("accounts");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(OneToMany.class));
      Assert.assertEquals(FetchType.EAGER, field.getAnnotation(OneToMany.class).getEnumValue(FetchType.class, "fetch"));
      Assert.assertEquals("Set", field.getType());
   }

}
