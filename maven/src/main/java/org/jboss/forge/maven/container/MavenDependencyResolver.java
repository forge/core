/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.jboss.forge.maven.dependency.Dependency;
import org.jboss.forge.maven.dependency.DependencyBuilder;
import org.jboss.forge.maven.dependency.DependencyFilter;
import org.jboss.forge.maven.dependency.DependencyQuery;
import org.jboss.forge.maven.dependency.DependencyRepository;
import org.jboss.forge.maven.dependency.DependencyResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

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
      MavenRepositorySystemSession session = setupRepoSession(system);

      Dependency dependency = query.getDependency();
      Artifact queryArtifact = dependencyToMavenArtifact(dependency);

      CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
               dependency.getScopeType()),
               convertToMavenRepos(query.getDependencyRepositories()));
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

   // Utility Methods

   private MavenRepositorySystemSession setupRepoSession(final RepositorySystem repoSystem)
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setOffline(false);
      Settings settings = container.getSettings();

      LocalRepository localRepo = new LocalRepository(new File(settings.getLocalRepository()), "");
      session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(localRepo));
      session.setTransferErrorCachingEnabled(false);
      session.setNotFoundCachingEnabled(false);

      return session;
   }

   private RemoteRepository convertToMavenRepo(final DependencyRepository repo)
   {
      RemoteRepository remoteRepository = new RemoteRepository(repo.getId(), "default", repo.getUrl());
      Settings settings = container.getSettings();
      Proxy activeProxy = settings.getActiveProxy();
      if (activeProxy != null)
      {
         Authentication auth = new Authentication(activeProxy.getUsername(), activeProxy.getPassword());
         remoteRepository.setProxy(new org.sonatype.aether.repository.Proxy(activeProxy.getProtocol(), activeProxy
                  .getHost(), activeProxy.getPort(), auth));
      }
      return remoteRepository;
   }

   private List<RemoteRepository> convertToMavenRepos(final List<DependencyRepository> repositories)
   {
      List<RemoteRepository> remoteRepos = new ArrayList<RemoteRepository>();
      for (DependencyRepository deprep : repositories)
      {
         remoteRepos.add(convertToMavenRepo(deprep));
      }
      return remoteRepos;
   }

   private Artifact dependencyToMavenArtifact(final Dependency dep)
   {
      Artifact artifact = new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getClassifier(),
               dep.getPackagingType() == null ? "jar" : dep.getPackagingType(), dep.getVersion());
      return artifact;
   }
}
