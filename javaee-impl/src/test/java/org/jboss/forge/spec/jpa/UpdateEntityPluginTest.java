/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

import java.io.InputStream;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.spec.javaee.jpa.UpdateEntityPlugin;
import org.junit.Test;

public class UpdateEntityPluginTest extends AbstractJPATest
{

   @Test
   public void testEqualsHashCodeWithBusinessKey() throws Exception
   {
      Project project = getProject();

      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource("EntityWithBusinessKey.java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      String businessKeyFieldName = "userName";

      queueInputLines(businessKeyFieldName, "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForObjects(businessKeyFieldName, javaClass);
      assertHashCodeStructureForObjects(businessKeyFieldName, javaClass);
   }

   @Test
   public void testEqualsHashCodeWithGeneratedValue() throws Exception
   {
      Project project = getProject();

      String className = "EntityOnlyIdAndVersion";
      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource(className + ".java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      String idFieldName = "id";

      queueInputLines(idFieldName, "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForObjects(idFieldName, javaClass);
      assertHashCodeStructureForObjects(idFieldName, javaClass);
      assertWarningForGeneratedValue(className, idFieldName);
   }

   @Test
   public void testEqualsHashCodeWithVersionField() throws Exception
   {
      Project project = getProject();

      String className = "EntityOnlyIdAndVersion";
      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource(className + ".java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      String versionFieldName = "version";

      queueInputLines(versionFieldName, "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForPrimitives(versionFieldName, javaClass);
      assertHashCodeStructureForPrimitives(versionFieldName, javaClass);
      assertWarningForVersionField(className, versionFieldName);
   }

   @Test
   public void testEqualsHashCodeWithTransient() throws Exception
   {
      Project project = getProject();

      String className = "EntityWithTransientField";
      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource(className + ".java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      String transientFieldName = "fullName";

      queueInputLines(transientFieldName, "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForObjects(transientFieldName, javaClass);
      assertHashCodeStructureForObjects(transientFieldName, javaClass);
      assertWarningForTransientField(className, transientFieldName);
   }

   @Test
   public void testEqualsHashCodeWithCollection() throws Exception
   {
      Project project = getProject();

      String className = "EntityWithCollection";
      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource(className + ".java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      JavaClass association = JavaParser.parse(JavaClass.class, getTestResource("Association.java"));
      java.saveJavaSource(association);
      String collectionFieldName = "associations";

      queueInputLines(collectionFieldName, "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForObjects(collectionFieldName, javaClass);
      assertHashCodeStructureForObjects(collectionFieldName, javaClass);
      assertWarningForCollectionField(className, collectionFieldName);
   }

   @Test
   public void testEqualsHashCodeWithMultipleFields() throws Exception
   {
      Project project = getProject();

      String className = "EntityWithMultipleFields";
      JavaClass entity = JavaParser.parse(JavaClass.class, getTestResource(className +".java"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      java.saveJavaSource(entity);
      JavaClass association = JavaParser.parse(JavaClass.class, getTestResource("Association.java"));
      java.saveJavaSource(association);
      String idFieldName = "id";
      String versionFieldName = "version";
      String transientFieldName = "fullName";
      String collectionFieldName = "associations";

      queueInputLines("*", "\n");
      getShell().execute(pluginName() + " hashcode-and-equals " + entity.getCanonicalName());
      JavaClass javaClass = (JavaClass) java.getJavaResource(entity.getQualifiedName()).getJavaSource();
      assertFalse(javaClass.hasSyntaxErrors());
      assertEqualsStructureForObjects(idFieldName, javaClass);
      assertHashCodeStructureForObjects(idFieldName, javaClass);
      assertEqualsStructureForPrimitives(versionFieldName, javaClass);
      assertHashCodeStructureForPrimitives(versionFieldName, javaClass);
      assertEqualsStructureForObjects(transientFieldName, javaClass);
      assertHashCodeStructureForObjects(transientFieldName, javaClass);
      assertEqualsStructureForObjects(collectionFieldName, javaClass);
      assertHashCodeStructureForObjects(collectionFieldName, javaClass);
      
      assertWarningForGeneratedValue(className, idFieldName);
      assertWarningForVersionField(className, versionFieldName);
      assertWarningForTransientField(className, transientFieldName);
      assertWarningForCollectionField(className, collectionFieldName);
   }

   private void assertEqualsStructureForObjects(String fieldName, JavaClass javaClass)
   {
      assertFalse(javaClass.getMethod("equals", Object.class).getBody().isEmpty());
      assertThat(javaClass.getMethod("equals", Object.class).getBody(), containsString("if (!" + fieldName
               + ".equals(other." + fieldName + "))"));
   }

   private void assertEqualsStructureForPrimitives(String fieldName, JavaClass javaClass)
   {
      assertFalse(javaClass.getMethod("equals", Object.class).getBody().isEmpty());
      assertThat(javaClass.getMethod("equals", Object.class).getBody(), containsString("if (" + fieldName
               + " != other." + fieldName + ")"));
   }

   private void assertHashCodeStructureForObjects(String fieldName, JavaClass javaClass)
   {
      assertFalse(javaClass.getMethod("hashCode").getBody().isEmpty());
      assertThat(javaClass.getMethod("hashCode").getBody(), containsString("result=prime * result + (("
               + fieldName + " == null) ? 0 : " + fieldName + ".hashCode());"));
   }

   private void assertHashCodeStructureForPrimitives(String fieldName, JavaClass javaClass)
   {
      assertFalse(javaClass.getMethod("hashCode").getBody().isEmpty());
      assertThat(javaClass.getMethod("hashCode").getBody(), containsString("result=prime * result + "
               + fieldName + ";"));
   }

   private void assertWarningForGeneratedValue(String className, String fieldName)
   {
      assertThat(
               getOutput(),
               containsString("***WARNING*** A field [" + fieldName
                        + "] having the @GeneratedValue annotation was chosen."
                        + " The generated equals() and hashCode() methods for the class [" + className
                        + "] may be incorrect."));
   }

   private void assertWarningForVersionField(String className, String fieldName)
   {
      assertThat(
               getOutput(),
               containsString("***WARNING*** A field [" + fieldName
                        + "] having the @Version annotation was chosen."
                        + " The generated equals() and hashCode() methods for the class [" + className
                        + "] may be incorrect."));
   }

   private void assertWarningForTransientField(String className, String fieldName)
   {
      assertThat(
               getOutput(),
               containsString("***WARNING*** A transient field [" + fieldName +
                        "] was chosen. The generated equals() and hashCode() methods for the class [" + className +
                        "] may be incorrect."));
   }

   private void assertWarningForCollectionField(String className, String fieldName)
   {
      assertThat(
               getOutput(),
               containsString("***WARNING*** A collection field [" + fieldName
                        + "] was chosen. The generated equals() and hashCode() methods for the class [" + className
                        + "] may be incorrect."));
   }

   private String pluginName()
   {
      return ConstraintInspector.getName(UpdateEntityPlugin.class);
   }

   private InputStream getTestResource(String file)
   {
      return getClass().getResourceAsStream(file);
   }
}
