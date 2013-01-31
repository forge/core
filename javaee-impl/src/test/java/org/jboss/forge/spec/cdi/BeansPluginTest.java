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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
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

   @Test(expected = RuntimeException.class)
   public void testCannotOverwriteBean() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-bean --type foo.beans.ExampleBean --scoped REQUEST";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleBean");

      Assert.assertTrue(resource.exists());
      getShell().execute(command);
   }

   @Test
   public void testOverwriteBean() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      getShell().execute("beans new-bean --type foo.beans.ExampleBean --scoped REQUEST");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleBean");

      Assert.assertTrue(resource.exists());
      Assert.assertTrue(JavaClass.class.cast(resource.getJavaSource()).hasAnnotation(RequestScoped.class));

      getShell().execute("beans new-bean --type foo.beans.ExampleBean --scoped APPLICATION --overwrite");
      // reload:
      resource = java.getJavaResource("foo.beans.ExampleBean");
      Assert.assertTrue(JavaClass.class.cast(resource.getJavaSource()).hasAnnotation(ApplicationScoped.class));
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
      // reload:
      resource = java.getJavaResource("foo.beans.ExampleQualifier");
      Assert.assertTrue(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Inherited.class));
   }

   @Test
   public void testNewStereotypeNoOptions() throws Exception
   {
      final boolean options = false;
      testNewStereotype(options);
   }

   @Test
   public void testNewStereotypeAllOptions() throws Exception
   {
      final boolean options = true;
      testNewStereotype(options);
   }

   private void testNewStereotype(boolean options) throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-stereotype --type foo.beans.ExampleStereotype";
      ElementType[] expectedElementTypes;
      if (options)
      {
         command += " --inherited --named --alternative --all-targets";
         expectedElementTypes = new ElementType[] { ElementType.TYPE, ElementType.METHOD, ElementType.FIELD };
      }
      else
      {
         queueInputLines("METHOD", "FIELD", "");
         expectedElementTypes = new ElementType[] { ElementType.METHOD, ElementType.FIELD };
      }
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleStereotype");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      Assert.assertTrue(source.isAnnotation());
      JavaAnnotation qualifier = (JavaAnnotation) source;
      Assert.assertTrue(qualifier.hasAnnotation(Stereotype.class));
      Assert.assertTrue(qualifier.getAnnotation(Stereotype.class).isMarker());
      Assert.assertTrue(qualifier.hasAnnotation(Target.class));
      Assert.assertArrayEquals(expectedElementTypes,
               qualifier.getAnnotation(Target.class).getEnumArrayValue(ElementType.class));

      if (options)
      {
         Assert.assertTrue(qualifier.hasAnnotation(Inherited.class));
         Assert.assertTrue(qualifier.getAnnotation(Inherited.class).isMarker());
         Assert.assertTrue(qualifier.hasAnnotation(Named.class));
         Assert.assertTrue(qualifier.getAnnotation(Named.class).isMarker());
         Assert.assertTrue(qualifier.hasAnnotation(Alternative.class));
         Assert.assertTrue(qualifier.getAnnotation(Alternative.class).isMarker());
      }
      else
      {
         Assert.assertFalse(qualifier.hasAnnotation(Inherited.class));
         Assert.assertFalse(qualifier.hasAnnotation(Named.class));
         Assert.assertFalse(qualifier.hasAnnotation(Alternative.class));
      }

      Assert.assertTrue(qualifier.hasAnnotation(Retention.class));
      Assert.assertSame(RetentionPolicy.RUNTIME,
               qualifier.getAnnotation(Retention.class).getEnumValue(RetentionPolicy.class));

      JavaAnnotation stub = JavaParser.parse(JavaAnnotation.class, "public @interface Stub {}");

      stub.addAnnotation(Target.class).setEnumArrayValue(expectedElementTypes);
      Assert.assertEquals(stub.getAnnotation(Target.class).getLiteralValue(), qualifier.getAnnotation(Target.class)
               .getLiteralValue());

      Assert.assertTrue(qualifier.hasAnnotation(Documented.class));
      Assert.assertTrue(qualifier.getAnnotation(Documented.class).isMarker());

      project.getFacet(CDIFacet.class).getConfig();
   }

   @Test(expected = RuntimeException.class)
   public void testCannotOverwriteStereotype() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-stereotype --all-targets --type foo.beans.ExampleStereotype";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleStereotype");

      Assert.assertTrue(resource.exists());
      getShell().execute(command);
   }

   @Test
   public void testOverwriteStereotype() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-stereotype --all-targets --type foo.beans.ExampleStereotype";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleStereotype");

      Assert.assertTrue(resource.exists());
      Assert.assertFalse(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Inherited.class));

      getShell().execute(command + " --inherited --overwrite");
      // reload:
      resource = java.getJavaResource("foo.beans.ExampleStereotype");
      Assert.assertTrue(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Inherited.class));
   }

   @Test
   public void testStereotypeTargets() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-stereotype --type foo.beans.ExampleStereotype";
      // demonstrate that combinations of TYPE + [FIELD | METHOD] are rejected:
      queueInputLines("", "TYPE", "FIELD", "", "TYPE", "METHOD", "", "TYPE", "");
      getShell().execute(command);
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleStereotype");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      Assert.assertTrue(source.isAnnotation());
      JavaAnnotation qualifier = (JavaAnnotation) source;
      Assert.assertTrue(qualifier.hasAnnotation(Stereotype.class));
      Assert.assertTrue(qualifier.getAnnotation(Stereotype.class).isMarker());
      Assert.assertTrue(qualifier.hasAnnotation(Target.class));
      Assert.assertArrayEquals(new ElementType[] { ElementType.TYPE },
               qualifier.getAnnotation(Target.class).getEnumArrayValue(ElementType.class));
   }

   @Test
   public void testNewPseudoScope() throws Exception
   {
      final boolean pseudo = true;
      final boolean passivating = false;
      testNewScope(pseudo, passivating);
   }

   @Test
   public void testNewNormalScope() throws Exception
   {
      final boolean pseudo = false;
      final boolean passivating = false;
      testNewScope(pseudo, passivating);
   }

   @Test
   public void testNewPassivatingScope() throws Exception
   {
      final boolean pseudo = false;
      final boolean passivating = true;
      testNewScope(pseudo, passivating);
   }
   
   @Test(expected = RuntimeException.class)
   public void testNewPassivatingPseudoScope() throws Exception
   {
      final boolean pseudo = true;
      final boolean passivating = true;
      testNewScope(pseudo, passivating);
   }
   
   private void testNewScope(boolean pseudo, boolean passivating) throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-scope --type foo.beans.ExampleScope";
      if (pseudo)
      {
         command += " --pseudo";
      }
      if (passivating)
      {
         command += " --passivating";
      }

      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleScope");

      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();

      Assert.assertNotNull(source);
      Assert.assertEquals("foo.beans", source.getPackage());
      Assert.assertTrue(source.isAnnotation());
      JavaAnnotation scope = (JavaAnnotation) source;

      if (pseudo)
      {
         Assert.assertFalse(scope.hasAnnotation(NormalScope.class));
         Assert.assertTrue(scope.hasAnnotation(Scope.class));
         Assert.assertTrue(scope.getAnnotation(Scope.class).isMarker());
      }
      else
      {
         Assert.assertFalse(scope.hasAnnotation(Scope.class));
         Assert.assertTrue(scope.hasAnnotation(NormalScope.class));
         if (passivating)
         {
            Assert.assertEquals(Boolean.toString(true), scope.getAnnotation(NormalScope.class).getLiteralValue("passivating"));
         }
         else
         {
            Assert.assertTrue(scope.getAnnotation(NormalScope.class).isMarker());
         }
      }

      Assert.assertTrue(scope.hasAnnotation(Retention.class));
      Assert.assertSame(RetentionPolicy.RUNTIME,
               scope.getAnnotation(Retention.class).getEnumValue(RetentionPolicy.class));

      Assert.assertTrue(scope.hasAnnotation(Target.class));
      JavaAnnotation stub = JavaParser.parse(JavaAnnotation.class, "public @interface Stub {}");
      stub.addAnnotation(Target.class).setEnumValue(ElementType.TYPE, ElementType.METHOD, ElementType.FIELD);
      Assert.assertEquals(stub.getAnnotation(Target.class).getLiteralValue(), scope.getAnnotation(Target.class)
               .getLiteralValue());

      Assert.assertTrue(scope.hasAnnotation(Documented.class));
      Assert.assertTrue(scope.getAnnotation(Documented.class).isMarker());

      project.getFacet(CDIFacet.class).getConfig();
   }

   @Test(expected = RuntimeException.class)
   public void testCannotOverwriteScope() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-scope --type foo.beans.ExampleScope";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleScope");

      Assert.assertTrue(resource.exists());
      getShell().execute(command);
   }

   @Test
   public void testOverwriteScope() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("y", "");
      getShell().execute("beans setup");
      String command = "beans new-scope --type foo.beans.ExampleScope";
      getShell().execute(command);

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("foo.beans.ExampleScope");

      Assert.assertTrue(resource.exists());
      Assert.assertTrue(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(NormalScope.class));
      Assert.assertFalse(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Scope.class));

      getShell().execute(command + " --overwrite --pseudo");
      // reload:
      resource = java.getJavaResource("foo.beans.ExampleScope");
      Assert.assertFalse(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(NormalScope.class));
      Assert.assertTrue(JavaAnnotation.class.cast(resource.getJavaSource()).hasAnnotation(Scope.class));
   }
}
