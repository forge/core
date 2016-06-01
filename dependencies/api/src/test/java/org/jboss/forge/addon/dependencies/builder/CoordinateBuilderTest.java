/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.builder;

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensures that {@link CoordinateBuilder} creates coordinates in the following schema:
 * 
 * {@code <groupId>:<artifactId>[:<packaging>[:<classifier>]]:<version>}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CoordinateBuilderTest
{
   @Test
   public void testCreateCoordinateFromString()
   {
      String coordinates = "org.jboss.forge.addon:projects-api:2.0.0.Final";
      CoordinateBuilder coord = CoordinateBuilder.create(coordinates);
      Assert.assertEquals("org.jboss.forge.addon", coord.getGroupId());
      Assert.assertEquals("projects-api", coord.getArtifactId());
      Assert.assertNull(coord.getPackaging());
      Assert.assertNull(coord.getClassifier());
      Assert.assertEquals("2.0.0.Final", coord.getVersion());
      Assert.assertEquals(coordinates, coord.toString());
   }

   @Test
   public void testCreateVersionlessCoordinateFromString()
   {
      String coordinates = "org.jboss.forge.addon:projects-api:";
      CoordinateBuilder coord = CoordinateBuilder.create(coordinates);
      Assert.assertEquals("org.jboss.forge.addon", coord.getGroupId());
      Assert.assertEquals("projects-api", coord.getArtifactId());
      Assert.assertNull(coord.getPackaging());
      Assert.assertNull(coord.getClassifier());
      Assert.assertNull(coord.getVersion());
      Assert.assertEquals(coordinates, coord.toString());
   }

   @Test
   public void testCreateCoordinateWithClassifierFromString()
   {
      String coordinates = "org.wildfly:wildfly-dist:zip:9.0.0.Final";
      CoordinateBuilder coord = CoordinateBuilder.create(coordinates);
      Assert.assertEquals("org.wildfly", coord.getGroupId());
      Assert.assertEquals("wildfly-dist", coord.getArtifactId());
      Assert.assertEquals("zip", coord.getPackaging());
      Assert.assertNull(coord.getClassifier());
      Assert.assertEquals("9.0.0.Final", coord.getVersion());
      Assert.assertEquals(coordinates, coord.toString());
   }

}
