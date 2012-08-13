/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Singleton;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
@RunWith(Arquillian.class)
public class JavaSourceFacetTest extends AbstractShellTest
{
   private static final String PKG = "com.test";

   @Test
   public void testCreateJavaFile() throws Exception
   {
      Project project = initializeJavaProject();

      String name = "JustCreated";
      JavaClass clazz = JavaParser.create(JavaClass.class).setName(name).setPackage(PKG);
      JavaResource file = project.getFacet(JavaSourceFacet.class).saveJavaSource(clazz);
      assertEquals(name + ".java", file.getName());

      JavaSource<?> result = file.getJavaSource();
      assertEquals(name, result.getName());
      assertEquals(PKG, result.getPackage());
      assertTrue(file.delete());
      assertEquals(clazz, result);
   }

   @Test
   public void testGetJavaClassReparsesJavaClass() throws Exception
   {
      Project project = initializeJavaProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String name = "JustCreated";
      JavaClass clazz = JavaParser.create(JavaClass.class).setName(name).setPackage(PKG);
      JavaResource file = java.saveJavaSource(clazz);
      assertEquals(name + ".java", file.getName());

      JavaSource<?> parsed = java.getJavaResource(clazz).getJavaSource();
      assertEquals(parsed.getName(), clazz.getName());
      assertEquals(parsed.getPackage(), clazz.getPackage());
      assertEquals(parsed, clazz);
   }

   @Test
   public void testGetTestJavaClassReparsesJavaClass() throws Exception
   {
      Project project = initializeJavaProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String name = "JustCreated";
      JavaClass clazz = JavaParser.create(JavaClass.class).setName(name).setPackage(PKG);
      JavaResource file = java.saveTestJavaSource(clazz);
      assertEquals(name + ".java", file.getName());

      JavaSource<?> parsed = java.getTestJavaResource(clazz).getJavaSource();
      assertEquals(parsed.getName(), clazz.getName());
      assertEquals(parsed.getPackage(), clazz.getPackage());
      assertEquals(parsed, clazz);
   }
}
