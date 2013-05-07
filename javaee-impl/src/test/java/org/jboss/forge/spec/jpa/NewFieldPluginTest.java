/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jpa;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.spec.javaee.jpa.FieldPlugin;
import org.jboss.forge.spec.javaee.jpa.PersistenceFacetImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewFieldPluginTest extends AbstractJPATest
{
   @Test
   public void testNewBoolean() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " boolean --named gamesPlayed --primitive false");
      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " boolean --named gamesWon");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertFalse(javaClass.getField("gamesPlayed").isPrimitive());
      assertEquals("Boolean", javaClass.getField("gamesPlayed").getType());
      assertTrue(javaClass.hasField("gamesWon"));
      assertTrue(javaClass.getField("gamesWon").isPrimitive());
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewCustomField() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class)
                        + " custom --named gamesPlayed --type org.jboss.CustomType");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertEquals("CustomType", javaClass.getField("gamesPlayed").getType());
      assertTrue(javaClass.hasImport("org.jboss.CustomType"));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewCustomFieldJavaExtension() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class)
                        + " custom --named gamesPlayed --type org.jboss.CustomType.java");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertEquals("CustomType", javaClass.getField("gamesPlayed").getType());
      assertTrue(javaClass.hasImport("org.jboss.CustomType"));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewTemporalField() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(ConstraintInspector.getName(FieldPlugin.class)
               + " temporal --named time --type TIME");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasField("time"));
      assertEquals("Date", javaClass.getField("time").getType());
      assertEquals(TemporalType.TIME,
               javaClass.getField("time").getAnnotation(Temporal.class).getEnumValue(TemporalType.class));
      assertTrue(javaClass.hasImport(TemporalType.class));
      assertTrue(javaClass.hasImport(Date.class));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewIntFieldObject() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " int --named gamesPlayed --primitive false");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasAnnotation(Entity.class));
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertFalse(javaClass.getField("gamesPlayed").isPrimitive());
      assertEquals("Integer", javaClass.getField("gamesPlayed").getType());
      assertFalse(javaClass.hasImport(Integer.class));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewIntFieldPrimitive() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " int --named gamesPlayed");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasAnnotation(Entity.class));
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertTrue(javaClass.getField("gamesPlayed").isPrimitive());
      assertEquals("int", javaClass.getField("gamesPlayed").getType());
      assertFalse(javaClass.hasImport(int.class));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewNumberField() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class)
                        + " number --named gamesPlayed --type java.math.BigDecimal");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasAnnotation(Entity.class));
      assertTrue(javaClass.hasField("gamesPlayed"));
      assertFalse(javaClass.getField("gamesPlayed").isPrimitive());
      assertEquals("BigDecimal", javaClass.getField("gamesPlayed").getType());
      assertTrue(javaClass.hasImport(BigDecimal.class));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewNumberFieldNotAddedIfClassNotValid() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);
      int originalSize = javaClass.getFields().size();

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class)
                        + " number --named gamesPlayed --type org.jboss.NotANumber");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertEquals(originalSize, javaClass.getFields().size());
      assertFalse(javaClass.hasImport("org.jboss.NotANumber"));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewLobField() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(ConstraintInspector.getName(FieldPlugin.class)
               + " lob --named image --length 10000");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertLobField(javaClass, "image", 10000);
   }

   @Test
   public void testNewLobFieldDefaultLength() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(ConstraintInspector.getName(FieldPlugin.class)
               + " lob --named image");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertLobField(javaClass, "image", Integer.MAX_VALUE);
   }

   private void assertLobField(JavaClass javaClass, String fieldName, int length)
   {
      assertTrue(javaClass.hasField(fieldName));
      Field<JavaClass> imageField = javaClass.getField(fieldName);
      assertEquals("byte", imageField.getType());
      assertTrue(javaClass.hasImport(Lob.class));
      assertTrue(imageField.hasAnnotation(Lob.class));
      assertTrue(imageField.hasAnnotation(Column.class));
      Annotation<JavaClass> columnAnnotation = imageField.getAnnotation(Column.class);
      assertEquals(String.valueOf(length), columnAnnotation.getLiteralValue("length"));
      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationship() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName());

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(OneToOne.class).getAnnotationValue("optional"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));
      assertTrue(rightEntity.getField("left").getType().equals(leftEntity.getName()));
      assertTrue(rightEntity.getField("left").hasAnnotation(OneToOne.class));
      assertEquals("right",
               rightEntity.getField("left").getAnnotation(OneToOne.class).getStringValue("mappedBy"));
      assertTrue(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationshipEagerFetch() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --fetchType EAGER");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertEquals(FetchType.EAGER,
               leftEntity.getField("right").getAnnotation(OneToOne.class).getEnumValue(FetchType.class, "fetch"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationshipNonOptional() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --required");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertEquals("false", leftEntity.getField("right").getAnnotation(OneToOne.class).getLiteralValue("optional"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationshipCascadeSingle() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade ALL");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertArrayEquals(
               new CascadeType[] { CascadeType.ALL },
               leftEntity.getField("right").getAnnotation(OneToOne.class)
                        .getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToOneRelationshipCascadeMultiple() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade PERSIST MERGE");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals(rightEntity.getName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(OneToOne.class));
      assertArrayEquals(new CascadeType[] { CascadeType.PERSIST, CascadeType.MERGE }, leftEntity.getField("right")
               .getAnnotation(OneToOne.class).getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManyRelationship() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName());

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getMethod("getRight").getReturnTypeInspector().toString()
               .equals("Set<" + rightEntity.getName() + ">"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManyRelationshipEagerFetch() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --fetchType EAGER");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getMethod("getRight").getReturnTypeInspector().toString()
               .equals("Set<" + rightEntity.getName() + ">"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertEquals(FetchType.EAGER,
               leftEntity.getField("right").getAnnotation(ManyToMany.class).getEnumValue(FetchType.class, "fetch"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManyRelationshipCascadeSingle() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade ALL");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getMethod("getRight").getReturnTypeInspector().toString()
               .equals("Set<" + rightEntity.getName() + ">"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertArrayEquals(
               new CascadeType[] { CascadeType.ALL },
               leftEntity.getField("right").getAnnotation(ManyToMany.class)
                        .getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManyRelationshipCascadeMultiple() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade PERSIST MERGE");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getMethod("getRight").getReturnTypeInspector().toString()
               .equals("Set<" + rightEntity.getName() + ">"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertArrayEquals(
               new CascadeType[] { CascadeType.PERSIST, CascadeType.MERGE },
               leftEntity.getField("right").getAnnotation(ManyToMany.class)
                        .getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManyRelationship() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName());

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManyRelationshipEagerFetch() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --fetchType EAGER");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals(FetchType.EAGER,
               leftEntity.getField("right").getAnnotation(OneToMany.class).getEnumValue(FetchType.class, "fetch"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManyRelationshipCascadeSingle() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade ALL");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertArrayEquals(
               new CascadeType[] { CascadeType.ALL },
               leftEntity.getField("right").getAnnotation(OneToMany.class)
                        .getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManyRelationshipMultiple() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade PERSIST MERGE");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertArrayEquals(
               new CascadeType[] { CascadeType.PERSIST, CascadeType.MERGE },
               leftEntity.getField("right").getAnnotation(OneToMany.class)
                        .getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationship() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName());

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("optional"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationshipEagerFetch() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --fetchType EAGER");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertEquals(FetchType.EAGER,
               leftEntity.getField("right").getAnnotation(ManyToOne.class).getEnumValue(FetchType.class, "fetch"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationshipNonOptional() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --required");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertEquals("false", leftEntity.getField("right").getAnnotation(ManyToOne.class).getLiteralValue("optional"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationshipCascadeSingle() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade ALL");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertArrayEquals(new CascadeType[] { CascadeType.ALL }, leftEntity.getField("right")
               .getAnnotation(ManyToOne.class).getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationshipCascadeMultiple() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName() + " --cascade PERSIST MERGE");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertArrayEquals(new CascadeType[] { CascadeType.PERSIST, CascadeType.MERGE }, leftEntity.getField("right")
               .getAnnotation(ManyToOne.class).getEnumArrayValue(CascadeType.class, "cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManyRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));

      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      // assertEquals("left",
      // leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));
      assertTrue(rightEntity.getField("left").hasAnnotation(ManyToMany.class));
      assertTrue(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManyRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertEquals("Set", leftEntity.getField("right").getType());
      assertEquals(rightEntity.getQualifiedName(), leftEntity.getField("right").getTypeInspector().getTypeArguments()
               .get(0).getQualifiedName());

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertEquals("left", leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals("CascadeType.ALL",
               leftEntity.getField("right").getAnnotation(OneToMany.class).getLiteralValue("cascade"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));
      assertEquals(leftEntity.getName(), rightEntity.getField("left").getType());
      assertTrue(rightEntity.getField("left").hasAnnotation(ManyToOne.class));
      assertNull(rightEntity.getField("left").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertTrue(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(ManyToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertTrue(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));

      assertEquals("Set", rightEntity.getField("left").getType());
      assertEquals(leftEntity.getQualifiedName(), rightEntity.getField("left").getTypeInspector().getTypeArguments()
               .get(0).getQualifiedName());

      assertTrue(rightEntity.getField("left").hasAnnotation(OneToMany.class));
      assertEquals("right", rightEntity.getField("left").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals("CascadeType.ALL",
               rightEntity.getField("left").getAnnotation(OneToMany.class).getLiteralValue("cascade"));
      assertTrue(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewOneToManySelfRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = rightEntity;

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " oneToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));

      assertEquals("Set", leftEntity.getField("right").getType());
      assertEquals(rightEntity.getQualifiedName(), leftEntity.getField("right").getTypeInspector().getTypeArguments()
               .get(0).getQualifiedName());

      assertTrue(leftEntity.getField("right").hasAnnotation(OneToMany.class));
      assertEquals("left", leftEntity.getField("right").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals("CascadeType.ALL",
               leftEntity.getField("right").getAnnotation(OneToMany.class).getLiteralValue("cascade"));
      assertFalse(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(OneToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));
      assertEquals(leftEntity.getName(), rightEntity.getField("left").getType());
      assertTrue(rightEntity.getField("left").hasAnnotation(ManyToOne.class));
      assertNull(rightEntity.getField("left").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(ManyToOne.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneSelfRelationshipInverse() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = rightEntity;

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + " --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertFalse(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));

      assertEquals("Set", rightEntity.getField("left").getType());
      assertEquals(leftEntity.getQualifiedName(), rightEntity.getField("left").getTypeInspector().getTypeArguments()
               .get(0).getQualifiedName());

      assertTrue(rightEntity.getField("left").hasAnnotation(OneToMany.class));
      assertEquals("right", rightEntity.getField("left").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals("CascadeType.ALL",
               rightEntity.getField("left").getAnnotation(OneToMany.class).getLiteralValue("cascade"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToManySelfRelationship() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity)
               .getJavaSource();

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToMany --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName());
      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertTrue(leftEntity.getField("right").getType().equals("Set"));
      assertTrue(leftEntity.getMethod("getRight").getReturnTypeInspector().toString()
               .equals("Set<" + rightEntity.getName() + ">"));
      assertTrue(leftEntity.getField("right").getTypeInspector().getTypeArguments().get(0).getQualifiedName()
               .equals(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToMany.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToMany.class).getStringValue("mappedBy"));
      assertFalse(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToMany.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertFalse(rightEntity.hasField("left"));
      assertTrue(rightEntity.hasImport(ManyToMany.class));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test
   public void testNewManyToOneSelfRelationshipInverseWithJavaExtension() throws Exception
   {
      Project project = getProject();
      JavaClass rightEntity = generateEntity(project);
      JavaClass leftEntity = rightEntity;

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + rightEntity.getName()
                        + ".java --inverseFieldName left");

      leftEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(leftEntity).getJavaSource();

      assertTrue(leftEntity.hasAnnotation(Entity.class));
      assertTrue(leftEntity.hasField("right"));
      assertEquals(rightEntity.getName(), leftEntity.getField("right").getType());
      assertTrue(leftEntity.getField("right").hasAnnotation(ManyToOne.class));
      assertNull(leftEntity.getField("right").getAnnotation(ManyToOne.class).getStringValue("mappedBy"));
      assertFalse(leftEntity.hasImport(rightEntity.getQualifiedName()));
      assertTrue(leftEntity.hasImport(ManyToOne.class));
      assertFalse(leftEntity.hasSyntaxErrors());

      rightEntity = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(rightEntity).getJavaSource();

      assertTrue(rightEntity.hasField("left"));

      assertEquals("Set", rightEntity.getField("left").getType());
      assertEquals(leftEntity.getQualifiedName(), rightEntity.getField("left").getTypeInspector().getTypeArguments()
               .get(0).getQualifiedName());

      assertTrue(rightEntity.getField("left").hasAnnotation(OneToMany.class));
      assertEquals("right", rightEntity.getField("left").getAnnotation(OneToMany.class).getStringValue("mappedBy"));
      assertEquals("CascadeType.ALL",
               rightEntity.getField("left").getAnnotation(OneToMany.class).getLiteralValue("cascade"));
      assertFalse(rightEntity.hasImport(leftEntity.getQualifiedName()));
      assertTrue(rightEntity.hasImport(OneToMany.class));
      assertFalse(rightEntity.hasSyntaxErrors());
   }

   @Test(expected = IllegalStateException.class)
   public void testNewManyToOneSelfRelationshipInverseWithJavaExtensionDuplicateAttributes() throws Exception
   {
      Project project = getProject();
      JavaClass entity = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + entity.getName()
                        + ".java --inverseFieldName left");
      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " manyToOne --named right --fieldType ~."
                        + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + "."
                        + entity.getName()
                        + ".java --inverseFieldName left");
   }

   @Test
   public void testAddAndRemoveField() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      getShell().execute(
               ConstraintInspector.getName(FieldPlugin.class) + " boolean --named gamesPlayed --primitive false");
      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " boolean --named gamesWon");

      queueInputLines("y", "y", "y");
      getShell().execute("rm gamesPlayed::Boolean");
      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertFalse(javaClass.hasField("gamesPlayed"));
      assertTrue(javaClass.hasField("gamesWon"));
      assertTrue(javaClass.getField("gamesWon").isPrimitive());
      assertFalse(javaClass.hasSyntaxErrors());

      getShell().execute("build");
   }

}
