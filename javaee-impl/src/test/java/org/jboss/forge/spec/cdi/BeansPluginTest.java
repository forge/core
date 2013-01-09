/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.cdi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class BeansPluginTest extends AbstractShellTest
{
   @Test
   public void testBeansSetup() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("n", "");
      getShell().execute("beans setup");

      project.getFacet(CDIFacet.class).getConfig();
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0")));
      Assert.assertTrue(project.getFacet(ResourceFacet.class).getResource("META-INF/beans.xml").exists());
   }

   @Test
   public void testBeansSetupProvidedScope() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");

      project.getFacet(CDIFacet.class).getConfig();
      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(
               DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0")));
      Assert.assertTrue(project.getFacet(ResourceFacet.class).getResource("META-INF/beans.xml").exists());
   }

   @Test
   public void testNewBean() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      getShell().execute("beans new-bean --type foo.beans.ExampleBean --scoped REQUEST");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleBean");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      project.getFacet(CDIFacet.class).getConfig();
   }

   @Test
   public void testNewQualifier() throws Exception
   {
      final boolean inherited = false;
      testNewQualifier(inherited);
   }

   @Test
   public void testNewQualifierInherited() throws Exception
   {
      final boolean inherited = true;
      testNewQualifier(inherited);
   }

   private void testNewQualifier(boolean inherited) throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-qualifier --type foo.beans.ExampleQualifier";
      if (inherited)
      {
         command += " --inherited true";
      }
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleQualifier");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      Assert.assertTrue(source.isAnnotation());
      JavaAnnotation qualifier = (JavaAnnotation) source;
      Assert.assertTrue(qualifier.hasAnnotation(Qualifier.class));
      Assert.assertTrue(qualifier.getAnnotation(Qualifier.class).isMarker());

      if (inherited)
      {
         Assert.assertTrue(qualifier.hasAnnotation(Inherited.class));
         Assert.assertTrue(qualifier.getAnnotation(Inherited.class).isMarker());
      }

      Assert.assertTrue(qualifier.hasAnnotation(Retention.class));
      Assert.assertSame(RetentionPolicy.RUNTIME,
               qualifier.getAnnotation(Retention.class).getEnumValue(RetentionPolicy.class));

      Assert.assertTrue(qualifier.hasAnnotation(Target.class));
      JavaAnnotation stub = JavaParser.parse(JavaAnnotation.class, "public @interface Stub {}");
      stub.addAnnotation(Target.class).setEnumValue(ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER,
               ElementType.TYPE);
      Assert.assertEquals(stub.getAnnotation(Target.class).getLiteralValue(), qualifier.getAnnotation(Target.class)
               .getLiteralValue());

      Assert.assertTrue(qualifier.hasAnnotation(Documented.class));
      Assert.assertTrue(qualifier.getAnnotation(Documented.class).isMarker());

      project.getFacet(CDIFacet.class).getConfig();
   }

   @Test(expected = RuntimeException.class)
   public void testCannotOverwriteQualifer() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-qualifier --type foo.beans.ExampleQualifier";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleQualifier");

      Assert.assertTrue(resource.exists());
      getShell().execute(command);
   }

   @Test
   public void testOverwriteQualifer() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-qualifier --type foo.beans.ExampleQualifier";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleQualifier");

      Assert.assertTrue(resource.exists());
      Assert.assertFalse(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Inherited.class));
      
      getShell().execute(command + " --inherited --overwrite");
      //reload:
      resource = java.getJavaResource("foo.beans.ExampleQualifier");
      Assert.assertTrue(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Inherited.class));
   }
}
