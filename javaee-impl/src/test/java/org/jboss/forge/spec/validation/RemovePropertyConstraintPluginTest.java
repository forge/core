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
package org.jboss.forge.spec.validation;

import static org.jboss.forge.shell.util.ConstraintInspector.getName;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.validation.constraints.NotNull;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.validation.RemovePropertyConstraintPlugin;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class RemovePropertyConstraintPluginTest extends SingletonAbstractShellTest
{
   private static final String PLUGIN_COMMAND = getName(RemovePropertyConstraintPlugin.class);

   private Project project;

   @Override
   @Before
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      project = initializeJavaProject();

      // install validation facet
      queueInputLines("", "");
      getShell().execute("validation setup");
   }

   @Test
   public void testRemoveConstraintOnProperty() throws Exception
   {
      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      JavaClass clazz = JavaParser.create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");
      clazz.addField("String foo;")
               .addAnnotation(NotNull.class);

      final JavaResource resource = facet.saveJavaSource(clazz);
      getShell().execute("pick-up " + resource.getFullyQualifiedName());

      // remove constraint on foo field
      getShell().execute(PLUGIN_COMMAND + " --property foo --named NotNull");

      // check if constraint has been successfully removed
      clazz = (JavaClass) resource.getJavaSource();
      final Field<JavaClass> field = clazz.getField("foo");

      assertNotNull(field);
      assertFalse(field.hasAnnotation(NotNull.class));
   }

   @Test
   public void testRemoveConstraintOnPropertyAccessor() throws Exception
   {
      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      JavaClass clazz = JavaParser.create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");
      clazz.addField("String foo;");
      clazz.addMethod("String getFoo(){return foo;}")
               .addAnnotation(NotNull.class);

      final JavaResource resource = facet.saveJavaSource(clazz);
      getShell().execute("pick-up " + resource.getFullyQualifiedName());

      // remove constraint on foo accessor
      getShell().execute(PLUGIN_COMMAND + " --property foo --named NotNull --onAccessor");

      // check if constraint has been successfully removed
      clazz = (JavaClass) resource.getJavaSource();
      final Method<JavaClass> method = clazz.getMethod("getFoo");

      assertNotNull(method);
      assertFalse(method.hasAnnotation(NotNull.class));
   }
}
