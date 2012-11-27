/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependency;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyFilter;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.DependencyQuery;
import org.jboss.forge.addon.dependency.DependencyRepository;
import org.jboss.forge.addon.dependency.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.maven.container.MavenContainer;
import org.jboss.forge.maven.container.MavenDependencyResolver;
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

   @Before
   public void setUp()
   {
      resolver = new MavenDependencyResolver(new MavenContainer());
   }

   @Test
   public void testResolveNonJarArtifact() throws Exception
   {

      CoordinateBuilder coordinate = CoordinateBuilder.create("org.jboss.forge:forge-example-plugin:2.0.0-SNAPSHOT")
               .setClassifier(MavenDependencyResolver.FORGE_ADDON_CLASSIFIER);
      DependencyQueryBuilder query = DependencyQueryBuilder.create(coordinate).setFilter(
               new DependencyFilter()
               {

                  @Override
                  public boolean accept(Dependency dependency)
                  {
                     return MavenDependencyResolver.FORGE_ADDON_CLASSIFIER.equals(dependency.getCoordinate()
                              .getClassifier());
                  }
               });
      Set<Dependency> artifacts = resolver.resolveDependencies(query);
      Assert.assertFalse(artifacts.isEmpty());
      Assert.assertEquals(1, artifacts.size());
      Dependency dependency = artifacts.iterator().next();
      Assert.assertEquals(MavenDependencyResolver.FORGE_ADDON_CLASSIFIER, dependency.getCoordinate().getClassifier());
      Assert.assertNotNull(dependency.getScopeType());
      Assert.assertTrue(dependency.isOptional());
   }

   @Test
   public void testResolveVersions() throws Exception
   {

      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder
               .create("org.jboss.forge:forge-distribution").setPackaging("zip")).setRepositories(
               new DependencyRepository("jboss", "https://repository.jboss.org/nexus/content/groups/public/"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveVersionsDependency() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder.create(CoordinateBuilder.create("org.hibernate:hibernate-core"));
      List<Coordinate> versions = resolver.resolveVersions(query);
      Assert.assertNotNull(versions);
      Assert.assertFalse(versions.isEmpty());
   }

   @Test
   public void testResolveArtifact() throws Exception
   {
      DependencyQuery query = DependencyQueryBuilder
               .create("org.jboss.forge:forge-example-plugin:jar:forge-addon:2.0.0-SNAPSHOT");
      File artifact = resolver.resolveArtifact(query);
      Assert.assertNotNull(artifact);
      Assert.assertTrue("Artifact does not exist: " + artifact, artifact.exists());
   }

   @Test
   public void testResolveNode() throws Exception
   {
      List<Dependency> addonDeps = resolver
               .resolveAddonDependencies("org.jboss.forge:forge-example-plugin:jar:forge-addon:2.0.0-SNAPSHOT");
      Assert.assertNotNull(addonDeps);
      Assert.assertEquals(1, addonDeps.size());
      Assert.assertEquals("commons-lang", addonDeps.get(0).getCoordinate().getArtifactId());
   }

   @Test
   public void testResolveDependencyHierarchy() throws Exception
   {
      DependencyNode root = resolver
               .resolveDependencyHierarchy("org.jboss.forge:forge-example-plugin:jar:forge-addon:2.0.0-SNAPSHOT");
      Assert.assertNotNull(root);
      // commons-lang and example2
      Assert.assertEquals(2, root.getChildren().size());
      System.out.println(root);
   }

   @Test
   public void testResolveDependencyHierarchy2() throws Exception
   {
      DependencyNode root = resolver
               .resolveDependencyHierarchy("org.hibernate:hibernate-core:4.0.0.Final");
      System.out.println(root);
   }

}
