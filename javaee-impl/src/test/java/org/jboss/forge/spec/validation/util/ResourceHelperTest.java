/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.validation.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.getJavaClassFromResource;
import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.hasAnnotation;

import java.io.FileNotFoundException;

import javax.validation.constraints.Null;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class ResourceHelperTest extends SingletonAbstractShellTest
{
   @Test
   public void testHasAnnotation() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      final JavaClass fooClass = JavaParser.create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");
      fooClass.addAnnotation(Null.class);

      final Field<JavaClass> fooField = fooClass.addField("private String foo;");
      fooField.addAnnotation(Null.class);

      final Method<JavaClass> fooMethod = fooClass.addMethod("private String getFoo(){return foo;}");
      fooMethod.addAnnotation(Null.class);

      final JavaResource resource = facet.saveJavaSource(fooClass);
      assertNotNull(resource);

      getShell().execute("pick-up " + resource.getFullyQualifiedName());
      assertTrue(hasAnnotation(getShell().getCurrentResource(), Null.class));

      getShell().execute("cd " + fooField.getName());
      assertTrue(hasAnnotation(getShell().getCurrentResource(), Null.class));

      getShell().execute("cd ../" + fooMethod.getName());
      assertTrue(hasAnnotation(getShell().getCurrentResource(), Null.class));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testHasAnnotationWithNullResource() throws FileNotFoundException
   {
      hasAnnotation(null, Null.class);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testHasAnnotationWithNullAnnotationClass() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      final JavaClass fooClass = JavaParser.create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");

      final JavaResource resource = facet.saveJavaSource(fooClass);
      assertNotNull(resource);

      hasAnnotation(resource, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testHasAnnotationWithNonJavaResource() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final ResourceFacet facet = project.getFacet(ResourceFacet.class);
      assertNotNull(facet);

      final Resource<?> resource = facet.createResource("Foo".toCharArray(), "Foo.txt");
      assertNotNull(resource);

      hasAnnotation(resource, Null.class);
   }

   @Test
   public void testGetJavaClassFromJavaResource() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      final JavaClass fooClass = JavaParser.create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");

      final JavaResource resource = facet.saveJavaSource(fooClass);
      assertNotNull(resource);

      assertEquals(fooClass, getJavaClassFromResource(resource));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetJavaClassFromResourceWithNullResource() throws FileNotFoundException
   {
      getJavaClassFromResource(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetJavaClassFromResourceWithNonJavaResource() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final ResourceFacet facet = project.getFacet(ResourceFacet.class);
      assertNotNull(facet);

      final Resource<?> resource = facet.createResource("Foo".toCharArray(), "Foo.txt");
      assertNotNull(resource);

      getJavaClassFromResource(resource);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testGetJavaClassFromResourceWithEnumResource() throws Exception
   {
      final Project project = initializeJavaProject();
      assertNotNull(project);

      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      final JavaEnum fooEnum = JavaParser.create(JavaEnum.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");

      final JavaResource resource = facet.saveJavaSource(fooEnum);
      assertNotNull(resource);

      getJavaClassFromResource(resource);
   }
}
