/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependencies;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.util.ProjectModelTest;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyMetadata;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RepositoryLookupTest extends ProjectModelTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ProjectModelTest.createTestArchive()
               .addAsManifestResource(
                        "META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider");
   }

   @Inject
   private DependencyResolver resolver;

   @Test
   public void testResolveVersions() throws Exception
   {
      Dependency dep = DependencyBuilder.create("com.ocpsoft:prettyfaces-jsf2");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.CENTRAL);
      List<Dependency> versions = resolver.resolveVersions(DependencyQueryBuilder.create(dep).setRepositories(repo));
      assertTrue(versions.size() > 4);
   }

   @Test
   public void testResolveVersionsStaticVersion() throws Exception
   {
      Dependency dep = DependencyBuilder.create("com.ocpsoft:prettyfaces-jsf2:3.2.0");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.CENTRAL);
      List<Dependency> versions = resolver.resolveVersions(DependencyQueryBuilder.create(dep).setRepositories(repo));
      assertTrue(versions.size() >= 1);
   }

   @Test
   public void testResolveVersionsStaticVersionSnapshot() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.jboss.errai.forge:forge-errai:1.0-SNAPSHOT");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      List<Dependency> versions = resolver.resolveVersions(DependencyQueryBuilder.create(dep).setRepositories(repo));
      assertTrue(versions.size() >= 1);
   }

   @Test
   public void testResolveArtifacts() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.jboss.errai.forge:forge-errai");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      List<DependencyResource> artifacts = resolver.resolveArtifacts(dep, Arrays.asList(repo));
      assertTrue(artifacts.size() >= 1);
   }

   @Test
   public void testResolveArtifactsSnapshotStaticVersion() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.jboss.errai.forge:forge-errai:[1.0-SNAPSHOT]");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      List<DependencyResource> artifacts = resolver.resolveArtifacts(dep, Arrays.asList(repo));
      assertTrue(artifacts.size() >= 1);
   }

   @Test
   public void testResolveArtifactsStaticVersion() throws Exception
   {
      Dependency dep = DependencyBuilder.create("com.ocpsoft:prettyfaces-jsf2:[3.2.0]");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.CENTRAL);
      List<DependencyResource> artifacts = resolver.resolveArtifacts(dep, Arrays.asList(repo));
      assertTrue(artifacts.size() >= 1);
   }

   @Test
   public void testResolveDependenciesStaticVersion() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.jboss.seam.international:seam-international:[3.0.0,)");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      List<DependencyResource> artifacts = resolver.resolveDependencies(dep, Arrays.asList(repo));
      assertTrue(artifacts.size() >= 1);
   }

   @Test
   public void testResolveNonJarArtifact() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.richfaces:richfaces-bom:4.0.0.Final")
               .setScopeType(ScopeType.IMPORT).setPackagingType("pom");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      List<DependencyResource> artifacts = resolver.resolveDependencies(dep, Arrays.asList(repo));
      assertTrue(artifacts.size() >= 1);
   }

   @Test
   public void testResolveDependencyMetadata() throws Exception
   {
      Dependency dep = DependencyBuilder.create("org.jboss.seam.international:seam-international:3.0.0.Final");
      DependencyRepository repo = new DependencyRepositoryImpl(KnownRepository.JBOSS_NEXUS);
      DependencyMetadata meta = resolver.resolveDependencyMetadata(dep, Arrays.asList(repo));
      assertTrue(meta.getDependencies().size() >= 1);
      assertTrue(meta.getManagedDependencies().size() >= 1);
   }
}
