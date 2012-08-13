/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.jpa.EntityPlugin;
import org.jboss.forge.spec.javaee.jpa.FieldPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewEntityPluginTest extends AbstractJPATest
{

   @Test
   public void testNewEntity() throws Exception
   {
      Project project = getProject();

      String entityName = "Goofy";
      queueInputLines("");
      getShell().execute(ConstraintInspector.getName(EntityPlugin.class) + " --named " + entityName);

      String pkg = project.getFacet(PersistenceFacet.class).getEntityPackage() + "." + entityName;
      String path = Packages.toFileSyntax(pkg) + ".java";
      JavaClass javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(path).getJavaSource();

      assertFalse(javaClass.hasSyntaxErrors());
      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasAnnotation(Entity.class));
      assertFalse(javaClass.hasSyntaxErrors());

      assertTrue(javaClass.hasImport(Entity.class));
      assertTrue(javaClass.hasField("id"));
      assertTrue(javaClass.hasField("version"));
      assertEquals("0", javaClass.getField("version").getLiteralInitializer());
      assertEquals("null", javaClass.getField("id").getLiteralInitializer());
      assertTrue(javaClass.toString().contains("implements Serializable"));
   }

   @Test
   public void testNewEntityCorrectsInvalidInput() throws Exception
   {
      Project project = getProject();
      JavaClass javaClass = generateEntity(project);

      queueInputLines("gamesWon");
      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " int --fieldName int");

      queueInputLines("gamesLost");
      getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " int --fieldName #$%#");

      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertTrue(javaClass.hasAnnotation(Entity.class));
      assertTrue(javaClass.hasImport(Entity.class));
      assertTrue(javaClass.hasField("gamesWon"));
      assertTrue(javaClass.hasField("gamesLost"));

      assertFalse(javaClass.hasSyntaxErrors());
   }

   @Test
   public void assertPackageOptionCreatesEntityInTheCorrectPackage() throws Exception
   {
      final Project project = getProject(); // setCurrentResource or setResource
      final String pkgName = "com.test.domain";
      final JavaClass entityClass = generateEntity(project, pkgName);

      final JavaClass entitySource = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(entityClass)
               .getJavaSource();

      assertEquals(entitySource.getPackage(), pkgName);
   }

   @Test
   public void assertEntityCreatedInPackageWhenWithinAPackage() throws Exception
   {
      final Project project = getProject();
      final String pkgName = project.getFacet(JavaSourceFacet.class).getBasePackage();
      getShell().setCurrentResource(project.getFacet(JavaSourceFacet.class).getBasePackageResource());
      final JavaClass entityClass = generateEntity(project);

      final JavaClass entitySource = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(entityClass)
               .getJavaSource();

      assertEquals(entitySource.getPackage(), pkgName);
   }

   @Test
   public void assertEntityClassDeclarationOnNewLine() throws Exception
   {
       Project project = getProject();
       JavaClass javaClass = generateEntity(project);

       String content = javaClass.toString();

       assertTrue(content.contains("@Entity" + System.getProperty("line.separator") +
               "public class"));
   }

   @Test
   public void testNewEntityIdStrategy() throws Exception
   {
      Project project = getProject();

      String entityName = "Goofy";
      queueInputLines("");
      getShell().execute(ConstraintInspector.getName(EntityPlugin.class) + " --named " + entityName + " --idStrategy TABLE");

      String pkg = project.getFacet(PersistenceFacet.class).getEntityPackage() + "." + entityName;
      String path = Packages.toFileSyntax(pkg) + ".java";
      JavaClass javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(path).getJavaSource();

      assertFalse(javaClass.hasSyntaxErrors());
      javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(javaClass).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());

      assertTrue(javaClass.hasImport(Entity.class));
      assertTrue(javaClass.hasField("id"));
      Field<JavaClass> idField = javaClass.getField("id");
      assertTrue(idField.hasAnnotation(GeneratedValue.class));
      Annotation<JavaClass> genValueAnn = idField.getAnnotation(GeneratedValue.class);
      assertEquals(GenerationType.TABLE, genValueAnn.getEnumValue(GenerationType.class, "strategy"));
   }

}
