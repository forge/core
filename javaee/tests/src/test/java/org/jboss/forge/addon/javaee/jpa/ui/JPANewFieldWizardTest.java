/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.beans.FieldOperations;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JPANewFieldWizardTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private UITestHarness uiTestHarness;

   @Inject
   private FieldOperations beanOperations;

   @Inject
   private ShellTest shellTest;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
   }

   @Test
   public void checkCommandMetadata() throws Exception
   {
      try (CommandController controller = uiTestHarness.createCommandController(JPANewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         // Checks the command metadata
         assertTrue(controller.getCommand() instanceof JPANewFieldWizard);
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals("JPA: New Field", metadata.getName());
         assertEquals("Java EE", metadata.getCategory().getName());
         assertEquals("JPA", metadata.getCategory().getSubCategory().getName());
         assertEquals(13, controller.getInputs().size());
         assertTrue(controller.hasInput("named"));
         assertTrue(controller.hasInput("targetEntity"));
         assertTrue(controller.hasInput("not-nullable"));
         assertTrue(controller.hasInput("not-updatable"));
         assertTrue(controller.hasInput("not-insertable"));
         assertTrue(controller.hasInput("type"));
         assertTrue(controller.hasInput("relationshipType"));
         assertTrue(controller.hasInput("lob"));
         assertTrue(controller.hasInput("length"));
         assertTrue(controller.hasInput("temporalType"));
         assertTrue(controller.hasInput("columnName"));
         assertTrue(controller.hasInput("enumType"));
         assertTrue(controller.hasInput("transient"));
      }
   }

   @Test
   public void testNewField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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

      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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

      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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

      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
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

   @Test
   public void testLobFieldWithByteArrayType() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "blob_field");
         controller.setValueFor("type", "byte[]");
         controller.setValueFor("lob", Boolean.TRUE);
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field blob_field created", result.getMessage());
      }

      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("blob_field"));
      final Field<?> field = javaClass.getField("blob_field");
      Assert.assertEquals("byte[]", field.getType().getName());
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals(String.valueOf(Integer.MAX_VALUE),
               field.getAnnotation(Column.class).getLiteralValue("length"));
      Assert.assertTrue(field.hasAnnotation(Lob.class));
   }

   @Test
   public void testLobFieldWithStringType() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "clob_field");
         controller.setValueFor("type", "String");
         controller.setValueFor("lob", Boolean.TRUE);
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);
         Assert.assertEquals("Field clob_field created", result.getMessage());
      }

      JavaClass<?> javaClass = entity.getJavaType();
      Assert.assertTrue(javaClass.hasField("clob_field"));
      final Field<?> field = javaClass.getField("clob_field");
      Assert.assertEquals("String", field.getType().getName());
      Assert.assertTrue(field.hasAnnotation(Column.class));
      Assert.assertEquals(String.valueOf(Integer.MAX_VALUE),
               field.getAnnotation(Column.class).getLiteralValue("length"));
      Assert.assertTrue(field.hasAnnotation(Lob.class));
   }

   @Test
   public void testPackageWildcardInTypeField() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      projectHelper.createEmptyEnum(project, "CustomerType");

      try (WizardCommandController controller = uiTestHarness.createWizardController(JPANewFieldWizard.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("targetEntity", entity);
         Assert.assertFalse(controller.canExecute());
         controller.setValueFor("named", "customerType");
         controller.setValueFor("type", "~.model.CustomerType");
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
   public void testShell() throws Exception
   {
      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
      shellTest.getShell().setCurrentResource(project.getRoot());
      Result result = shellTest.execute(
               "jpa-new-field --named firstName --target-entity " + entity.getJavaType().getQualifiedName(), 10,
               TimeUnit.SECONDS);
      Assert.assertThat(result, not(instanceOf(Failed.class)));
      JavaClassSource javaClassSource = entity.getJavaType();
      Assert.assertThat(javaClassSource.hasField("firstName"), is(true));
   }

}