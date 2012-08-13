/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import junit.framework.Assert;

import org.jboss.forge.project.packaging.PackagingType;
import org.junit.Test;

public class DependencyImplTest
{

   @Test
   public void testEqualityEmpty()
   {
      DependencyImpl left = new DependencyImpl();
      DependencyImpl right = new DependencyImpl();

      Assert.assertEquals(left, right);
   }

   @Test
   public void testEquality()
   {
      DependencyImpl left = new DependencyImpl();
      left.setArtifactId("org.test.dep");
      left.setGroupId("org.test");

      DependencyImpl right = new DependencyImpl();
      right.setArtifactId("org.test.dep");
      right.setGroupId("org.test");

      Assert.assertEquals(left, right);
   }

   @Test
   public void testEquality2()
   {
      DependencyImpl left = new DependencyImpl();
      left.setArtifactId("weld-api-bom");
      left.setGroupId("org.jboss.weld");
      left.setPackagingType(PackagingType.BASIC);
      left.setScopeType(ScopeType.IMPORT);
      left.setVersion("1.1.Final");

      DependencyImpl right = new DependencyImpl();
      right.setArtifactId("weld-api-bom");
      right.setGroupId("org.jboss.weld");
      right.setPackagingType(PackagingType.BASIC);
      right.setScopeType(ScopeType.IMPORT);
      right.setVersion("1.1.Final");

      Assert.assertEquals(left, right);
   }

}
