/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jboss.forge.project.packaging.PackagingType;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyBuilderTest
{
   @Test
   public void testCreateWithIdentifier() throws Exception
   {
      DependencyBuilder dep = DependencyBuilder.create("org.jboss.forge: seam-forge :9:test");

      assertEquals("org.jboss.forge", dep.getGroupId());
      assertEquals("seam-forge", dep.getArtifactId());
      assertEquals("9", dep.getVersion());
      assertEquals(ScopeType.TEST, dep.getScopeTypeEnum());
   }

   @Test
   public void testCreateWithPartialIdentifier() throws Exception
   {
      DependencyBuilder dep = DependencyBuilder.create("org.jboss.forge: seam-forge :9 ::pom");

      assertEquals("org.jboss.forge", dep.getGroupId());
      assertEquals("seam-forge", dep.getArtifactId());
      assertEquals("9", dep.getVersion());
      assertNull(dep.getScopeType());
      assertEquals(PackagingType.BASIC, dep.getPackagingTypeEnum());
   }

   @Test
   public void testCreateWithPartialIdentifier2() throws Exception
   {
      DependencyBuilder dep = DependencyBuilder.create("org.jboss.forge");

      assertEquals("org.jboss.forge", dep.getGroupId());
      assertNull(dep.getArtifactId());
      assertNull(dep.getVersion());
      assertNull(dep.getScopeType());
   }

   @Test
   public void testCreateWithPartialAndEmptyIdentifier() throws Exception
   {
      DependencyBuilder dep = DependencyBuilder.create("org.jboss.forge::");

      assertEquals("org.jboss.forge", dep.getGroupId());
      assertNull(dep.getArtifactId());
      assertNull(dep.getVersion());
      assertNull(dep.getScopeType());
   }
}
