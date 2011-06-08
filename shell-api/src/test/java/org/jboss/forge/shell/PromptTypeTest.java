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

package org.jboss.forge.shell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.shell.util.Patterns;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PromptTypeTest
{
   @Test
   public void testJavaPackage() throws Exception
   {
      assertTrue(PromptType.JAVA_PACKAGE.matches("org"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org."));
      assertTrue(PromptType.JAVA_PACKAGE.matches("org.jboss.forge"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.jboss."));
      assertTrue(PromptType.JAVA_PACKAGE.matches("org.jboss_project"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.jboss_$f00"));
   }

   @Test
   public void testJavaClass() throws Exception
   {
      assertTrue(PromptType.JAVA_CLASS.matches("org.jboss.forge.spec.validation.MockMessageInterpolator"));
   }

   @Test
   public void testJavaPackageCannotContainKeywords() throws Exception
   {
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.jboss.package"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.jboss.private"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.public"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("public"));
      assertFalse(PromptType.JAVA_PACKAGE.matches("org.synchronized.foo"));
   }

   @Test
   public void testJavaVariableName() throws Exception
   {
      assertTrue(PromptType.JAVA_VARIABLE_NAME.matches("gamesPlayed"));
      assertFalse(PromptType.JAVA_VARIABLE_NAME.matches("(*#$%"));
      assertFalse(PromptType.JAVA_VARIABLE_NAME.matches("public"));
      assertTrue(PromptType.JAVA_VARIABLE_NAME.matches("privateIpAddress"));
   }

   @Test
   public void testPatternsReservedWords() throws Exception
   {
      assertTrue("for".matches(Patterns.JAVA_KEYWORDS));
      assertTrue("private".matches(Patterns.JAVA_KEYWORDS));
      assertTrue("package".matches(Patterns.JAVA_KEYWORDS));
      assertTrue("class".matches(Patterns.JAVA_KEYWORDS));
      assertTrue("while".matches(Patterns.JAVA_KEYWORDS));
      assertTrue("volatile".matches(Patterns.JAVA_KEYWORDS));
   }

   @Test
   public void testDependencyId() throws Exception
   {
      assertFalse(PromptType.DEPENDENCY_ID.matches("group.id"));
      assertTrue(PromptType.DEPENDENCY_ID.matches("group.id:artifact.id"));
      assertTrue(PromptType.DEPENDENCY_ID.matches("group.id:artifact.id:1.0.0"));
      assertTrue(PromptType.DEPENDENCY_ID.matches("group.id:artifact.id:2.0.0-SNAPSHOT:scope"));
      assertTrue(PromptType.DEPENDENCY_ID.matches("group.id:artifact.id:3.0.Final:scope:packaging"));
      assertFalse(PromptType.DEPENDENCY_ID.matches("group.id:artifact.id:3.0.Final:scope:packaging:extra"));
   }

}
