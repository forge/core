/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
