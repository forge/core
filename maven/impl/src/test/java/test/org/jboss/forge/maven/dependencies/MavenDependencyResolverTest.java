/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.maven.dependencies;

import java.util.List;
import java.util.Set;

import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.DependencyNode;
import org.jboss.forge.dependencies.DependencyQuery;
import org.jboss.forge.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
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
      resolver = new MavenDependencyResolver(new FileResourceFactory(), new MavenContainer());
   }

   @Test
   public void testResolveClassifiedArtifact() throws Exception
   {
      CoordinateBuilder coordinate = CoordinateBuilder.create("org.jboss.forge:resources:2.0.0-SNAPSHOT")
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
   // TODO it would be nice to figure out how to resolve all artifacts with a given groupId
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
   public void testResolveVersionsDependency() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge:maven-api"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveArtifact() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder
               .create("org.jboss.forge:resources:jar:forge-addon:2.0.0-SNAPSHOT");
      Dependency artifact = resolver.resolveArtifact(query);
      Assert.assertNotNull(artifact);
      Assert.assertTrue("Artifact does not exist: " + artifact, artifact.getArtifact().exists());
   }

   @Test
   public void testResolveDependencyHierarchy() throws Exception
   {
      DependencyNode root = resolver
               .resolveDependencyHierarchy(DependencyQueryBuilder
                        .create("org.jboss.forge:resources:jar:forge-addon:2.0.0-SNAPSHOT"));
      Assert.assertNotNull(root);
      Assert.assertEquals(5, root.getChildren().size());
      Assert.assertEquals("convert", root.getChildren().get(1).getDependency().getCoordinate().getArtifactId());
      Assert.assertEquals("ui-hints", root.getChildren().get(2).getDependency().getCoordinate().getArtifactId());
   }

   @Test
   public void testResolveProvidedAddons() throws Exception
   {
      DependencyNode root = resolver.resolveAddonDependencyHierarchy(DependencyQueryBuilder
               .create("org.jboss.forge:addons:jar:forge-addon:2.0.0-SNAPSHOT"));
      Assert.assertNotNull(root);
      Assert.assertEquals(2, root.getChildren().size());
   }

   @Test
   public void testResolveProvidedAddons2() throws Exception
   {
      DependencyNode root = resolver.resolveAddonDependencyHierarchy(DependencyQueryBuilder
               .create("org.jboss.forge:resources:jar:forge-addon:2.0.0-SNAPSHOT"));
      Assert.assertNotNull(root);
      Assert.assertEquals(5, root.getChildren().size());
   }
}
