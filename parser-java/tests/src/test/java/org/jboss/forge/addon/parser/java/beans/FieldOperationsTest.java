/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.projects.JavaProjectType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FieldOperationsTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addClass(FieldOperationsTest.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven")
               );
   }

   @Inject
   private ProjectFactory projectFactory;

   private FieldOperations fieldOperations;

   private JavaClassSource targetClass;

   @Before
   public void setUp()
   {
      fieldOperations = new FieldOperations();
      targetClass = Roaster.parse(JavaClassSource.class, "public class Test{}");
   }

   /**
    * Verifies that simple fields can be added to a class.
    */
   @Test
   public void testAddNewSimpleField() throws Exception
   {
      String simpleTypeName = String.class.getSimpleName();
      String qualifiedTypeName = String.class.getCanonicalName();

      // Add a String field with a qualified type.
      String fieldName = "firstName";
      fieldOperations.addFieldTo(targetClass, qualifiedTypeName, fieldName);

      // Is the field present? Is the name and type of the field correct?
      assertThat(targetClass.hasField(fieldName), is(true));
      assertThat(targetClass.getField(fieldName).getName(), equalTo(fieldName));
      assertThat(targetClass.getField(fieldName).getType().getName(), equalTo(simpleTypeName));
      // Is the type from java.lang imported?
      assertThat(targetClass.hasImport(qualifiedTypeName), is(false));
      // Syntax errors?
      assertThat(targetClass.hasSyntaxErrors(), is(false));

      // Add a String field with a simple type.
      fieldName = "lastName";
      fieldOperations.addFieldTo(targetClass, simpleTypeName, fieldName);

      // Is the field present? Is the name and type of the field correct?
      assertThat(targetClass.hasField(fieldName), is(true));
      assertThat(targetClass.getField(fieldName).getName(), equalTo(fieldName));
      assertThat(targetClass.getField(fieldName).getType().getName(), equalTo(simpleTypeName));
      // Is the type from java.lang imported?
      assertThat(targetClass.hasImport(qualifiedTypeName), is(false));
      // Syntax errors?
      assertThat(targetClass.hasSyntaxErrors(), is(false));
   }

   @Test
   public void testGetterAndSetterAreAdded() throws Exception
   {
      fieldOperations.addFieldTo(targetClass, "int", "age");

      assertThat(targetClass.hasMethodSignature("getAge"), is(true));
      assertThat(targetClass.hasMethodSignature("setAge", "int"), is(true));
   }

   @Test
   public void testAddFieldsWithAnnotations() throws Exception
   {
      String annotationName = "javax.persistence.Column";
      fieldOperations.addFieldTo(targetClass, "String", "firstName", annotationName);
      assertThat(targetClass.hasImport(annotationName), is(true));
      assertThat(targetClass.getField("firstName").hasAnnotation(annotationName), is(true));
   }

   @Test
   public void testAddFieldsWithoutGetterOrSetter() throws Exception
   {
      fieldOperations.addFieldTo(targetClass, "String", "firstName", Visibility.PRIVATE,
               false, false);
      assertThat(targetClass.hasMethodSignature("getFirstName"), is(false));
      assertThat(targetClass.hasMethodSignature("setFirstName", "String"), is(false));

      fieldOperations.addFieldTo(targetClass, "String", "lastName", Visibility.PRIVATE,
               false, true);
      assertThat(targetClass.hasMethodSignature("getLastName"), is(false));
      assertThat(targetClass.hasMethodSignature("setLastName", "String"), is(true));

      fieldOperations.addFieldTo(targetClass, "int", "age", Visibility.PRIVATE,
               true, false);
      assertThat(targetClass.hasMethodSignature("getAge"), is(true));
      assertThat(targetClass.hasMethodSignature("setAge", "int"), is(false));
   }

   @Test
   public void testAddFieldWithDifferentVisibility() throws Exception
   {
      fieldOperations.addFieldTo(targetClass, "String", "firstName", Visibility.PUBLIC, true, true);
      assertThat(targetClass.getField("firstName").getVisibility(), is(Visibility.PUBLIC));

      fieldOperations.addFieldTo(targetClass, "String", "lastName", Visibility.PROTECTED, true, true);
      assertThat(targetClass.getField("lastName").getVisibility(), is(Visibility.PROTECTED));

      fieldOperations.addFieldTo(targetClass, "int", "age", Visibility.PACKAGE_PRIVATE, true, true);
      assertThat(targetClass.getField("age").getVisibility(), is(Visibility.PACKAGE_PRIVATE));
   }

   @Test
   public void testIsFieldEnum() throws Exception
   {
      JavaProjectType javaProjectType = new JavaProjectType();
      Project project = projectFactory.createTempProject(javaProjectType.getRequiredFacets());
      JavaClassSource targetClass = Roaster.parse(JavaClassSource.class, "public class Test{}");
      JavaEnumSource testEnum = Roaster.create(JavaEnumSource.class).setName("TestEnum");

      String testPackage = "org.jboss.forge.testpkg";

      targetClass.setPackage(testPackage);
      testEnum.setPackage(testPackage);

      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      javaSourceFacet.saveJavaSource(targetClass);
      javaSourceFacet.saveJavaSource(testEnum);

      assertThat(fieldOperations.isFieldTypeEnum(project, targetClass, "TestEnum"), is(true));
      assertThat(fieldOperations.isFieldTypeEnum(project, targetClass, "org.jboss.forge.testpkg.TestEnum"), is(true));

      assertThat(fieldOperations.isFieldTypeEnum(project, targetClass, "NotExist"), is(false));
      assertThat(fieldOperations.isFieldTypeEnum(project, targetClass, "Test"), is(false));
      assertThat(fieldOperations.isFieldTypeEnum(project, targetClass, "org.jboss.forge.testpkg.Test"), is(false));

      assertThat(fieldOperations.isFieldTypeEnum(project, "TestEnum"), is(false));
      assertThat(fieldOperations.isFieldTypeEnum(project, "org.jboss.forge.testpkg.TestEnum"), is(true));

      assertThat(fieldOperations.isFieldTypeEnum(project, "NotExist"), is(false));
      assertThat(fieldOperations.isFieldTypeEnum(project, "Test"), is(false));
      assertThat(fieldOperations.isFieldTypeEnum(project, "org.jboss.forge.testpkg.Test"), is(false));
   }
}
