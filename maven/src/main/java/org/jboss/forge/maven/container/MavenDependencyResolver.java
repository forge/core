/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import static org.jboss.forge.maven.container.MavenConvertUtils.convertToMavenRepos;
import static org.jboss.forge.maven.container.MavenConvertUtils.coordinateToMavenArtifact;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Settings;
import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyFilter;
import org.jboss.forge.addon.dependency.DependencyQuery;
import org.jboss.forge.addon.dependency.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependency.builder.DependencyBuilder;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.graph.TreeDependencyVisitor;
import org.sonatype.aether.version.Version;

@Singleton
public class MavenDependencyResolver implements DependencyResolver
{
   private MavenContainer container;
   public static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

   @Inject
   public MavenDependencyResolver(MavenContainer container)
   {
      super();
      this.container = container;
   }

   @Override
   public Set<Dependency> resolveDependencies(DependencyQuery query)
   {
      Set<Dependency> result = new HashSet<Dependency>();
      DependencyFilter filter = query.getDependencyFilter();
      RepositorySystem system = container.lookup(RepositorySystem.class);
      Settings settings = container.getSettings();

      MavenRepositorySystemSession session = setupRepoSession(system, settings);

      Artifact queryArtifact = coordinateToMavenArtifact(query.getCoordinate());

      List<RemoteRepository> remoteRepos = convertToMavenRepos(query.getDependencyRepositories(), settings);
      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));

      CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
               query.getScopeType()), remoteRepos);

      DependencyRequest request = new DependencyRequest(collectRequest, null);

      DependencyResult artifacts;
      try
      {
         artifacts = system.resolveDependencies(session, request);
      }
      catch (DependencyResolutionException e)
      {
         throw new RuntimeException(e);
      }
      DependencyNode root = artifacts.getRoot();
      for (DependencyNode node : root.getChildren())
      {
         Dependency d = MavenConvertUtils.convertToDependency(node);
         if (filter == null || filter.accept(d))
         {
            result.add(d);
         }

      }
      return result;
   }

   @Override
   public List<Coordinate> resolveVersions(DependencyQuery query)
   {
      VersionRangeResult r = getVersions(query);
      List<Coordinate> result = new ArrayList<Coordinate>();
      DependencyFilter filter = query.getDependencyFilter();
      for (Version v : r.getVersions())
      {
         CoordinateBuilder coord = CoordinateBuilder.create(query.getCoordinate()).setVersion(v.toString());
         DependencyBuilder versionedDep = DependencyBuilder.create().setCoordinate(coord);
         if (filter == null || filter.accept(versionedDep))
         {
            result.add(coord);
         }
      }
      return result;
   }

   /**
    * Returns the versions of a specific artifact
    *
    * @param query
    * @return
    */
   private VersionRangeResult getVersions(DependencyQuery query)
   {
      Coordinate dep = query.getCoordinate();
      try
      {
         String version = dep.getVersion();
         if (version == null || version.isEmpty())
         {
            dep = CoordinateBuilder.create(dep).setVersion("[,)");
         }
         else if (!version.matches("(\\(|\\[).*?(\\)|\\])"))
         {
            dep = CoordinateBuilder.create(dep).setVersion("[" + version + "]");
         }

         RepositorySystem maven = container.lookup(RepositorySystem.class);
         Settings settings = container.getSettings();

         MavenRepositorySystemSession session = setupRepoSession(maven, settings);

         Artifact artifact = coordinateToMavenArtifact(dep);

         List<RemoteRepository> remoteRepos = convertToMavenRepos(query.getDependencyRepositories(), settings);
         remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));

         VersionRangeRequest rangeRequest = new VersionRangeRequest(artifact, remoteRepos, null);

         VersionRangeResult rangeResult = maven.resolveVersionRange(session, rangeRequest);
         return rangeResult;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to look up versions for [" + dep + "]", e);
      }
   }

   private MavenRepositorySystemSession setupRepoSession(final RepositorySystem repoSystem, final Settings settings)
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setOffline(false);

      LocalRepository localRepo = new LocalRepository(new File(settings.getLocalRepository()), "");
      session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(localRepo));
      session.setTransferErrorCachingEnabled(false);
      session.setNotFoundCachingEnabled(false);

      return session;
   }

   @Override
   public File resolveArtifact(DependencyQuery query)
   {
      RepositorySystem system = container.lookup(RepositorySystem.class);
      Settings settings = container.getSettings();

      List<RemoteRepository> remoteRepos = convertToMavenRepos(query.getDependencyRepositories(), settings);
      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));

      MavenRepositorySystemSession session = setupRepoSession(system, settings);

      Artifact queryArtifact = coordinateToMavenArtifact(query.getCoordinate());
      ArtifactRequest request = new ArtifactRequest(queryArtifact, remoteRepos, null);
      try
      {
         ArtifactResult resolvedArtifact = system.resolveArtifact(session, request);
         Artifact artifact = resolvedArtifact.getArtifact();
         return artifact.getFile();
      }
      catch (ArtifactResolutionException e)
      {
         throw new MavenOperationException(e);
      }
   }

   public List<Dependency> resolveAddonDependencies(String coordinates)
   {
      try
      {
         RepositorySystem system = container.lookup(RepositorySystem.class);
         Settings settings = container.getSettings();
         MavenRepositorySystemSession session = setupRepoSession(system, settings);
         final CoordinateBuilder coord = CoordinateBuilder.create(coordinates);
         Artifact queryArtifact = coordinateToMavenArtifact(coord);
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
                  null), container.getEnabledRepositoriesFromProfile(settings));

         DependencyRequest dr = new DependencyRequest(collectRequest, null);
         DependencyResult result = system.resolveDependencies(session, dr);
         List<Dependency> collect = new ArrayList<Dependency>();
         DependencyVisitor visitor = new TreeDependencyVisitor(new AddonDependencyVisitor(coord.getGroupId(),
                  coord.getArtifactId(), collect));
         result.getRoot().accept(visitor);
         return collect;
      }
      catch (DependencyResolutionException e)
      {
         throw new RuntimeException("Could not resolve dependencies for addon [" + coordinates + "]", e);
      }
   }

   @Override
   public MavenDependencyNode resolveDependencyHierarchy(String coordinates)
   {
      try
      {
         RepositorySystem system = container.lookup(RepositorySystem.class);
         Settings settings = container.getSettings();
         MavenRepositorySystemSession session = setupRepoSession(system, settings);
         final CoordinateBuilder coord = CoordinateBuilder.create(coordinates);
         Artifact queryArtifact = coordinateToMavenArtifact(coord);
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
                  null), container.getEnabledRepositoriesFromProfile(settings));

         DependencyRequest dr = new DependencyRequest(collectRequest, null);
         DependencyResult result = system.resolveDependencies(session, dr);
         return MavenConvertUtils.toDependencyNode(result.getRoot());
      }
      catch (DependencyResolutionException e)
      {
         throw new RuntimeException("Could not resolve dependencies for addon [" + coordinates + "]", e);
      }
   }

   private class AddonDependencyVisitor implements DependencyVisitor
   {
      private String addonGroupId;
      private String addonArtifactId;
      private List<Dependency> dependencies;

      public AddonDependencyVisitor(String addonGroupId, String addonArtifactId, List<Dependency> listCollector)
      {
         super();
         this.addonGroupId = addonGroupId;
         this.addonArtifactId = addonArtifactId;
         this.dependencies = listCollector;
      }

      @Override
      public boolean visitEnter(DependencyNode node)
      {
         Artifact artifact = node.getDependency().getArtifact();
         // If it is the
         if (addonGroupId.equals(artifact.getGroupId()) && addonArtifactId.equals(artifact.getArtifactId()))
         {
            return true;
         }
         else
         {
            if (FORGE_ADDON_CLASSIFIER.equals(artifact.getClassifier()))
            {
               return false;
            }
            else
            {
               dependencies.add(MavenConvertUtils.convertToDependency(node));
               return true;
            }
         }
      }

      @Override
      public boolean visitLeave(DependencyNode node)
      {
         return true;
      }
   }
}
