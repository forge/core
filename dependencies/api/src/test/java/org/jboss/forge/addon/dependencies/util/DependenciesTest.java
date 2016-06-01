/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.util;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Dependencies} test methods
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class DependenciesTest
{

   /**
    * Test method for
    * {@link org.jboss.forge.addon.dependencies.util.Dependencies#areEquivalent(org.jboss.forge.addon.dependencies.Dependency, org.jboss.forge.addon.dependencies.Dependency)}
    * .
    */
   @Test
   public void testAreEquivalentDependencyDependency()
   {
      Dependency depOne = DependencyBuilder.create().setGroupId("org.jboss.errai").setArtifactId("errai-cdi-client")
               .setPackaging("jar");
      Dependency depTwo = DependencyBuilder.create().setGroupId("org.jboss.errai").setArtifactId("errai-cdi-client")
               .setPackaging("test-jar");
      Assert.assertFalse(Dependencies.areEquivalent(depOne, depTwo));
   }

   /**
    * Test method for
    * {@link org.jboss.forge.addon.dependencies.util.Dependencies#areEquivalent(org.jboss.forge.addon.dependencies.Coordinate, org.jboss.forge.addon.dependencies.Coordinate)}
    * .
    */
   @Test
   public void testAreEquivalentCoordinateCoordinate()
   {
      Dependency depOne = DependencyBuilder.create().setGroupId("org.jboss.errai").setArtifactId("errai-cdi-client")
               .setPackaging("jar");
      Dependency depTwo = DependencyBuilder.create().setGroupId("org.jboss.errai").setArtifactId("errai-cdi-client")
               .setPackaging("test-jar");
      Assert.assertFalse(Dependencies.areEquivalent(depOne, depTwo));
   }

}
