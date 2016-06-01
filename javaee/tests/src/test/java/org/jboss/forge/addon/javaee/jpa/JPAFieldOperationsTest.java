/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

/**
 * Unit tests to verify the behavior of the {@link JPAFieldOperations} implementation class.
 */
@RunWith(Arquillian.class)
public class JPAFieldOperationsTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ProjectHelper projectHelper;

   private Project project;

   private JavaResource entity;

   private JavaClassSource entityClass;

   @Inject
   private JPAFieldOperations jpaFieldOperations;

   @Before
   public void setUp()
   {
      project = projectHelper.createJavaLibraryProject();
      projectHelper.installJPA_2_0(project);
      try
      {
         entity = projectHelper.createJPAEntity(project, "Customer");
         entityClass = entity.getJavaType();
      }
      catch (IOException ioEx)
      {
         throw new IllegalStateException("Failed to setup the test suite.", ioEx);
      }
   }

   @Test
   public void testAddOneToManyRel() throws Exception
   {
      JavaResource rhsClass = projectHelper.createJPAEntity(project, "Account");
      String qualifiedRhsType = rhsClass.getJavaType().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaType().getName();
      String simpleCollectionType = Set.class.getSimpleName();
      String qualifiedCollectionType = Set.class.getCanonicalName();

      String fieldName = "accounts";
      jpaFieldOperations.newOneToManyRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, Lists.<CascadeType> newArrayList());
      entityClass = entity.getJavaType();

      // Is the field present? Is the name and type of the field and it's parameterized type correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      FieldSource<JavaClassSource> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType().getName(), equalTo(simpleCollectionType));
      assertThat(field.getType().getTypeArguments().get(0).getName(), equalTo(simpleRhsType));

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
      String qualifiedRhsType = rhsClass.getJavaType().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaType().getName();
      String simpleCollectionType = Set.class.getSimpleName();
      String qualifiedCollectionType = Set.class.getCanonicalName();

      String fieldName = "accounts";
      jpaFieldOperations.newManyToManyRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, Lists.<CascadeType> newArrayList());
      entityClass = entity.getJavaType();

      // Is the field present? Is the name and type of the field and it's parameterized type correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      FieldSource<JavaClassSource> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType().getName(), equalTo(simpleCollectionType));
      assertThat(field.getType().getTypeArguments().get(0).getName(), equalTo(simpleRhsType));

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
      String qualifiedRhsType = rhsClass.getJavaType().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaType().getName();

      String fieldName = "store";
      jpaFieldOperations.newManyToOneRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, false, Lists.<CascadeType> newArrayList());
      entityClass = entity.getJavaType();

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      FieldSource<JavaClassSource> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType().getName(), equalTo(simpleRhsType));

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
      String qualifiedRhsType = rhsClass.getJavaType().getCanonicalName();
      String simpleRhsType = rhsClass.getJavaType().getName();

      String fieldName = "store";
      jpaFieldOperations.newOneToOneRelationship(project, entity, fieldName, qualifiedRhsType, null,
               FetchType.LAZY, false, Lists.<CascadeType> newArrayList());
      entityClass = entity.getJavaType();

      // Is the field present? Is the name and type of the field correct?
      assertThat(entityClass.hasField(fieldName), is(true));
      FieldSource<JavaClassSource> field = entityClass.getField(fieldName);
      assertThat(field.getName(), equalTo(fieldName));
      assertThat(field.getType().getName(), equalTo(simpleRhsType));

      // Is the @OneToOne annotation present ?
      assertThat(field.hasAnnotation(OneToOne.class), is(true));

      // Is the RHS type imported?
      assertThat(entityClass.hasImport(qualifiedRhsType), is(true));

      // Syntax errors?
      assertThat(entityClass.hasSyntaxErrors(), is(false));
   }
}
