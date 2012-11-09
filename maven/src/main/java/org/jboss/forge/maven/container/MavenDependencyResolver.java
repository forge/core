/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import static org.jboss.forge.maven.container.MavenConvertUtils.convertToMavenRepos;
import static org.jboss.forge.maven.container.MavenConvertUtils.dependencyToMavenArtifact;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Settings;
import org.jboss.forge.maven.dependency.Dependency;
import org.jboss.forge.maven.dependency.DependencyBuilder;
import org.jboss.forge.maven.dependency.DependencyFilter;
import org.jboss.forge.maven.dependency.DependencyQuery;
import org.jboss.forge.maven.dependency.DependencyResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.version.Version;

@Singleton
public class MavenDependencyResolver implements DependencyResolver
{
   private MavenContainer container;

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

      Dependency dependency = query.getDependency();
      Artifact queryArtifact = dependencyToMavenArtifact(dependency);

      List<RemoteRepository> remoteRepos = convertToMavenRepos(query.getDependencyRepositories(), settings);
      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));

      CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
               dependency.getScopeType()), remoteRepos);

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
         org.sonatype.aether.graph.Dependency artifactDependency = node.getDependency();
         Artifact artifact = artifactDependency.getArtifact();
         File file = artifact.getFile();
         Dependency d = DependencyBuilder.create().setArtifactId(artifact.getArtifactId())
                  .setGroupId(artifact.getGroupId()).setVersion(artifact.getVersion())
                  .setPackagingType(artifact.getExtension()).setArtifact(file)
                  .setOptional(artifactDependency.isOptional())
                  .setClassifier(artifact.getClassifier())
                  .setScopeType(artifactDependency.getScope());
         if (filter == null || filter.accept(d))
         {
            result.add(d);
         }

      }
      return result;
   }

   @Override
   public List<Dependency> resolveVersions(DependencyQuery query)
   {
      Dependency dep = query.getDependency();
      VersionRangeResult r = getVersions(query);
      List<Dependency> result = new ArrayList<Dependency>();
      DependencyFilter filter = query.getDependencyFilter();
      for (Version v : r.getVersions())
      {
         DependencyBuilder versionedDep = DependencyBuilder.create(dep).setVersion(v.toString());
         if (filter == null || filter.accept(versionedDep))
         {
            result.add(versionedDep);
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
      Dependency dep = query.getDependency();
      try
      {
         String version = dep.getVersion();
         if (version == null || version.isEmpty())
         {
            dep = DependencyBuilder.create(dep).setVersion("[,)");
         }
         else if (!version.matches("(\\(|\\[).*?(\\)|\\])"))
         {
            dep = DependencyBuilder.create(dep).setVersion("[" + version + "]");
         }

         RepositorySystem maven = container.lookup(RepositorySystem.class);
         Settings settings = container.getSettings();

         MavenRepositorySystemSession session = setupRepoSession(maven, settings);

         Artifact artifact = dependencyToMavenArtifact(dep);

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
}
