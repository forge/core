/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import com.google.common.collect.Lists;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.IOException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests to verify the behavior of the {@link FieldOperations} class.
 */
@RunWith(Arquillian.class)
public class FieldOperationsTest
{

   @Deployment
   @Dependencies({
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
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven")
               );
   }

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   private JavaResource entity;

   private JavaClass entityClass;

   @Inject
   private FieldOperations fieldOperations;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
      try
      {
         entity = projectHelper.createJPAEntity(project, "Customer");
         entityClass = (JavaClass) entity.getJavaSource();
      }
      catch (IOException ioEx)
      {
         throw new IllegalStateException("Failed to setup the test suite.", ioEx);
      }
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
      fieldOperations.addFieldTo(entityClass, qualifiedTypeName, fieldName);

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      assertThat(entityClass.getField(fieldName).getName(), equalTo(fieldName));
      assertThat(entityClass.getField(fieldName).getType(), equalTo(simpleTypeName));
      // Is the type from java.lang imported?
      assertThat(entityClass.hasImport(qualifiedTypeName), is(false));
      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));

      // Add a String field with a simple type.
      fieldName = "lastName";
      fieldOperations.addFieldTo(entityClass, simpleTypeName, fieldName);

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      assertThat(entityClass.getField(fieldName).getName(), equalTo(fieldName));
      assertThat(entityClass.getField(fieldName).getType(), equalTo(simpleTypeName));
      // Is the type from java.lang imported?
      assertThat(entityClass.hasImport(qualifiedTypeName), is(false));
      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }

   @Test
   public void testAddOneToManyRel() throws Exception
   {
      JavaResource rhsClass = projectHelper.createJPAEntity(project, "Account");
      String qualifiedRhsType = rhsClass.getJavaSource().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaSource().getName();
      String simpleCollectionType = Set.class.getSimpleName();
      String qualifiedCollectionType = Set.class.getCanonicalName();

      String fieldName = "accounts";
      fieldOperations.newOneToManyRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, Lists.<CascadeType> newArrayList());
      entityClass = (JavaClass) entity.getJavaSource();

      // Is the field present? Is the name and type of the field and it's parameterized type correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      Field<JavaClass> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType(), equalTo(simpleCollectionType));
      assertThat(field.getTypeInspector().getTypeArguments().get(0).getName(), equalTo(simpleRhsType));

      // Is the @OneToMany annotation present ?
      assertThat(field.hasAnnotation(OneToMany.class), is(true));

      // Is the collection type and generic type imported?
      assertThat(entityClass.hasImport(qualifiedRhsType), is(true));
      assertThat(entityClass.hasImport(qualifiedCollectionType), is(true));
      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }

   @Test
   public void testAddManyToManyRel() throws Exception
   {
      JavaResource rhsClass = projectHelper.createJPAEntity(project, "Account");
      String qualifiedRhsType = rhsClass.getJavaSource().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaSource().getName();
      String simpleCollectionType = Set.class.getSimpleName();
      String qualifiedCollectionType = Set.class.getCanonicalName();

      String fieldName = "accounts";
      fieldOperations.newManyToManyRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, Lists.<CascadeType> newArrayList());
      entityClass = (JavaClass) entity.getJavaSource();

      // Is the field present? Is the name and type of the field and it's parameterized type correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      Field<JavaClass> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType(), equalTo(simpleCollectionType));
      assertThat(field.getTypeInspector().getTypeArguments().get(0).getName(), equalTo(simpleRhsType));

      // Is the @ManyToMany annotation present ?
      assertThat(field.hasAnnotation(ManyToMany.class), is(true));

      // Is the collection type and generic type imported?
      assertThat(entityClass.hasImport(qualifiedRhsType), is(true));
      assertThat(entityClass.hasImport(qualifiedCollectionType), is(true));
      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }

   @Test
   public void testAddManyToOneRel() throws Exception
   {
      JavaResource rhsClass = projectHelper.createJPAEntity(project, "Store");
      String qualifiedRhsType = rhsClass.getJavaSource().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaSource().getName();

      String fieldName = "store";
      fieldOperations.newManyToOneRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, false, Lists.<CascadeType> newArrayList());
      entityClass = (JavaClass) entity.getJavaSource();

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      Field<JavaClass> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType(), equalTo(simpleRhsType));

      // Is the @ManyToOne annotation present ?
      assertThat(field.hasAnnotation(ManyToOne.class), is(true));

      // Is the RHS type imported?
      assertThat(entityClass.hasImport(qualifiedRhsType), is(true));

      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }

   @Test
   public void testAddOneToOneRel() throws Exception
   {
      JavaResource rhsClass = projectHelper.createJPAEntity(project, "Store");
      String qualifiedRhsType = rhsClass.getJavaSource().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaSource().getName();

      String fieldName = "store";
      fieldOperations.newOneToOneRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, false, Lists.<CascadeType> newArrayList());
      entityClass = (JavaClass) entity.getJavaSource();

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      Field<JavaClass> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType(), equalTo(simpleRhsType));

      // Is the @OneToOne annotation present ?
      assertThat(field.hasAnnotation(OneToOne.class), is(true));

      // Is the RHS type imported?
      assertThat(entityClass.hasImport(qualifiedRhsType), is(true));

      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }

}
