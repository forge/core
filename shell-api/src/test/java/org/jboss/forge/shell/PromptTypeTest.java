/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
      assertTrue(PromptType.JAVA_PACKAGE.matches(""));
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
