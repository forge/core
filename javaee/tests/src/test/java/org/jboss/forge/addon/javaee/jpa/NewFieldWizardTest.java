/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.ui.NewFieldWizard;
import org.jboss.forge.addon.javaee.jpa.ui.RelationshipType;
import org.jboss.forge.addon.parser.java.beans.FieldOperations;
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
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
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

   @Inject
   private FieldOperations beanOperations;

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
               project.getRoot()))
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
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<?> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType().getName());
      Assert.assertEquals("FIRST_NAME_COLUMN", field.getAnnotation(Column.class).getStringValue("name"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("nullable"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("updatable"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("insertable"));
   }

   @Test
   public void testNewFieldWithNotNullableInsertableUpdatableTrue() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "firstName");
         controller.setValueFor("columnName", "FIRST_NAME_COLUMN");
         controller.setValueFor("not-nullable", "true");
         controller.setValueFor("not-insertable", "true");
         controller.setValueFor("not-updatable", "true");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field firstName created", result.getMessage());
      }
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<?> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType().getName());
      Assert.assertEquals("FIRST_NAME_COLUMN", field.getAnnotation(Column.class).getStringValue("name"));
      Assert.assertEquals("false", field.getAnnotation(Column.class).getLiteralValue("nullable"));
      Assert.assertEquals("false", field.getAnnotation(Column.class).getLiteralValue("updatable"));
      Assert.assertEquals("false", field.getAnnotation(Column.class).getLiteralValue("insertable"));
   }


   @Test
   public void testNewFieldWithNotNullableInsertableUpdatableFalse() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "firstName");
         controller.setValueFor("columnName", "FIRST_NAME_COLUMN");
         controller.setValueFor("not-nullable", "false");
         controller.setValueFor("not-insertable", "false");
         controller.setValueFor("not-updatable", "false");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field firstName created", result.getMessage());
      }
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<?> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType().getName());
      Assert.assertEquals("FIRST_NAME_COLUMN", field.getAnnotation(Column.class).getStringValue("name"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("nullable"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("updatable"));
      Assert.assertNull(field.getAnnotation(Column.class).getLiteralValue("insertable"));
   }

   @Test
   public void testNewTransientField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
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
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<?> field = javaClass.getField("firstName");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(Transient.class));
      Assert.assertEquals("String", field.getType().getName());
   }

   @Test
   public void testUpdateExistingField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaClassSource javaSource = entity.getJavaType();
      beanOperations.addFieldTo(javaSource, "String", "firstName");
      entity.setContents(javaSource.toString());
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
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
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("firstName"));
      final Field<?> field = javaClass.getField("firstName");
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("String", field.getType().getName());
      Assert.assertEquals("100", field.getAnnotation(Column.class).getStringValue("length"));
   }

   @Test
   public void testNewOneToManyField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource otherEntity = projectHelper.createJPAEntity(project, "Account");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "accounts");
         controller.setValueFor("type", otherEntity.getJavaType().getCanonicalName());
         controller.setValueFor("relationshipType", RelationshipType.ONE_TO_MANY);
         Assert.assertTrue(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         assertThat("Result should be of type CompositeResult", result, instanceOf(CompositeResult.class));
         CompositeResult compositeResult = (CompositeResult) result;
         Assert.assertEquals("Relationship One-to-Many created", compositeResult.getResults().get(1).getMessage());
      }
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("accounts"));
      final Field<?> field = javaClass.getField("accounts");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(OneToMany.class));
      Assert.assertEquals("Set", field.getType().getName());
   }
   
   @Test
   public void testEmbeddedRelationship() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource otherEntity = projectHelper.createJPAEmbeddable(project, "Account");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "accounts");
         controller.setValueFor("type", otherEntity.getJavaType().getCanonicalName());
         controller.setValueFor("relationshipType", RelationshipType.EMBEDDED);
         Assert.assertTrue(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         assertThat("Result should be of type CompositeResult", result, instanceOf(CompositeResult.class));
         CompositeResult compositeResult = (CompositeResult) result;
         Assert.assertEquals("Relationship Embedded created", compositeResult.getResults().get(1).getMessage());
      }
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("accounts"));
      final Field<?> field = javaClass.getField("accounts");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(Embedded.class));
      Assert.assertEquals("Account", field.getType().getName());
   }

   @Test
   public void testNewOneToManyEagerFetchField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource otherEntity = projectHelper.createJPAEntity(project, "Account");
      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "accounts");
         controller.setValueFor("type", otherEntity.getJavaType().getCanonicalName());
         controller.setValueFor("relationshipType", RelationshipType.ONE_TO_MANY);
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next();
         controller.setValueFor("fetchType", FetchType.EAGER);
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         assertThat("Result should be of type CompositeResult", result, instanceOf(CompositeResult.class));
         CompositeResult compositeResult = (CompositeResult) result;
         Assert.assertEquals("Relationship One-to-Many created", compositeResult.getResults().get(1).getMessage());
      }
      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("accounts"));
      final Field<?> field = javaClass.getField("accounts");
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(OneToMany.class));
      Assert.assertEquals(FetchType.EAGER, field.getAnnotation(OneToMany.class).getEnumValue(FetchType.class, "fetch"));
      Assert.assertEquals("Set", field.getType().getName());
   }

   @Test
   public void testNewEnumField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource enumEntity = projectHelper.createEmptyEnum(project, "CustomerType");

      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "customerType");
         controller.setValueFor("type", enumEntity.getJavaType().getCanonicalName());
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field customerType created", result.getMessage());
      }

      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("customerType"));
      final Field<?> field = javaClass.getField("customerType");
      Assert.assertEquals("CustomerType", field.getType().getName());
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(Enumerated.class));
      Assert.assertTrue(field.getAnnotation(Enumerated.class).getValues().isEmpty());
   }

   @Test
   public void testNewEnumFieldWithColumnName() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource enumEntity = projectHelper.createEmptyEnum(project, "CustomerType");

      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "customerType");
         controller.setValueFor("type", enumEntity.getJavaType().getCanonicalName());
         controller.setValueFor("columnName", "CUSTOMER_TYPE_COLUMN");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field customerType created", result.getMessage());
      }

      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("customerType"));
      final Field<?> field = javaClass.getField("customerType");
      Assert.assertEquals("CustomerType", field.getType().getName());
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals("CUSTOMER_TYPE_COLUMN", field.getAnnotation(Column.class).getStringValue("name"));
      Assert.assertTrue(field.hasAnnotation(Enumerated.class));
      Assert.assertTrue(field.getAnnotation(Enumerated.class).getValues().isEmpty());
   }

   @Test
   public void testNewEnumFieldWithType() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      JavaResource enumEntity = projectHelper.createEmptyEnum(project, "CustomerType");

      try (WizardCommandController controller = uiTestHarness.createWizardController(NewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "customerType");
         controller.setValueFor("type", enumEntity.getJavaType().getCanonicalName());
         controller.setValueFor("enumType", EnumType.STRING);
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field customerType created", result.getMessage());
      }

      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("customerType"));
      final Field<?> field = javaClass.getField("customerType");
      Assert.assertEquals("CustomerType", field.getType().getName());
      Assert.assertFalse(field.hasAnnotation(Column.class));
      Assert.assertTrue(field.hasAnnotation(Enumerated.class));
      Assert.assertFalse(field.getAnnotation(Enumerated.class).getValues().isEmpty());
      Assert.assertEquals(EnumType.STRING, field.getAnnotation(Enumerated.class).getEnumValue(EnumType.class));
   }
}