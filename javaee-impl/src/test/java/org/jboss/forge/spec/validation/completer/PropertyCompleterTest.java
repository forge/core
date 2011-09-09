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
package org.jboss.forge.spec.validation.completer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.jboss.forge.parser.JavaParser.create;

import java.io.FileNotFoundException;
import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.validation.completer.PropertyCompleter;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class PropertyCompleterTest extends SingletonAbstractShellTest
{
   @Before
   @Override
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();
   }

   @Test
   public void testCompletionOnClassWithProperties() throws Exception
   {
      // creates Foo class
      final JavaResource createdResource = createFooClass(getProject(), "property1", "property2", "property3");
      getShell().execute("pick-up " + createdResource.getFullyQualifiedName());

      final PropertyCompleter propertyCompleter = new PropertyCompleter(getShell());
      final List<String> properties = propertyCompleter.getCompletionTokens();

      assertEquals(3, properties.size());
      assertEquals("property1", properties.get(0));
      assertEquals("property2", properties.get(1));
      assertEquals("property3", properties.get(2));
   }

   @Test
   public void testCompletionOnClassWithoutProperties() throws Exception
   {
      // creates a Foo class
      final JavaResource createdResource = createFooClass(getProject());
      getShell().execute("pick-up " + createdResource.getFullyQualifiedName());

      final PropertyCompleter propertyCompleter = new PropertyCompleter(getShell());
      final List<String> properties = propertyCompleter.getCompletionTokens();

      assertTrue(properties.isEmpty());
   }

   private JavaResource createFooClass(final Project project, final String... propertyNames)
            throws FileNotFoundException
   {
      assertTrue(project.hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(facet);

      // create the foo class
      final JavaClass fooClass = create(JavaClass.class)
               .setPackage(facet.getBasePackage())
               .setName("Foo");

      for (String onePropertyName : propertyNames)
      {
         fooClass.addField("String " + onePropertyName + "");
      }

      // save the class
      return facet.saveJavaSource(fooClass);
   }
}
