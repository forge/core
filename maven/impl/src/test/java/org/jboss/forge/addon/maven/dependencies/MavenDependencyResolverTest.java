/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.dependencies;

import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyMetadata;
import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.furnace.util.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenDependencyResolverTest
{
   private MavenDependencyResolver resolver;
   private Predicate<Dependency> addonFilter = new Predicate<Dependency>()
   {
      @Override
      public boolean accept(Dependency dependency)
      {
         return "forge-addon".equals(dependency.getCoordinate().getClassifier());
      }
   };

   @Before
   public void setUp()
   {
      resolver = new MavenDependencyResolver(new FileResourceFactory());
   }

   @Test
   public void testResolveClassifiedArtifact() throws Exception
   {
      CoordinateBuilder coordinate = CoordinateBuilder.create("org.jboss.forge:resources:2.0.0.Alpha3")
               .setClassifier("forge-addon");
      DependencyQueryBuilder query = DependencyQueryBuilder.create(coordinate).setFilter(addonFilter);
      Set<Dependency> artifacts = resolver.resolveDependencies(query);
      Assert.assertFalse(artifacts.isEmpty());
      Assert.assertEquals(3, artifacts.size());
      Dependency dependency = artifacts.iterator().next();
      Assert.assertEquals("forge-addon", dependency.getCoordinate().getClassifier());
      Assert.assertNotNull(dependency.getScopeType());
   }

   @Test(expected = RuntimeException.class)
   public void testResolveWildcardArtifactId() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder.create().setGroupId("org.jboss.forge")
               .setArtifactId("").setClassifier("forge-addon"));
      Set<Dependency> coreAddons = resolver.resolveDependencies(query);
      Assert.assertFalse(coreAddons.isEmpty());
   }

   @Test
   public void testResolveVersions() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge:dependencies-api"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveVersions2() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge.addon:maven-api"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveVersionsDependency() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge:resources-api"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveArtifact() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder
               .create("org.jboss.forge:resources:jar:forge-addon:2.0.0.Alpha3");
      Dependency artifact = resolver.resolveArtifact(query);
      Assert.assertNotNull(artifact);
      Assert.assertTrue("Artifact does not exist: " + artifact, artifact.getArtifact().exists());
   }

   @Test
   public void testResolveMetadata() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder
               .create("org.jboss.forge:resources:jar:forge-addon:2.0.0.Alpha3");
      DependencyMetadata metadata = resolver.resolveDependencyMetadata(query);
      Assert.assertNotNull(metadata);
      Assert.assertTrue(metadata.getDependencies().contains(
               DependencyBuilder.create("junit:junit:4.11")));
   }

   @Test
   public void testResolveDependencyHierarchy() throws Exception
   {
      DependencyNode root = resolver
               .resolveDependencyHierarchy(DependencyQueryBuilder
                        .create("org.jboss.forge:resources:jar:forge-addon:2.0.0.Alpha3"));
      Assert.assertNotNull(root);
      Assert.assertEquals(5, root.getChildren().size());
      Assert.assertEquals("convert", root.getChildren().get(1).getDependency().getCoordinate().getArtifactId());
      // TODO: ui-hints was changed to ui-spi since 2.0.0.Alpha5
      Assert.assertEquals("ui-hints", root.getChildren().get(2).getDependency().getCoordinate().getArtifactId());
   }
}
