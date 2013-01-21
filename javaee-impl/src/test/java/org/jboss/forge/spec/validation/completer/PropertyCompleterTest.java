/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation.completer;

import static org.jboss.forge.parser.JavaParser.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
