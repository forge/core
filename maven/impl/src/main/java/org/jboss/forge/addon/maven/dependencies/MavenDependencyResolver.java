/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.dependencies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.version.Version;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyException;
import org.jboss.forge.addon.dependencies.DependencyMetadata;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyNodeBuilder;
import org.jboss.forge.addon.maven.util.MavenConvertUtils;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.manager.maven.MavenOperationException;
import org.jboss.forge.furnace.manager.maven.util.MavenRepositories;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.forge.furnace.util.Strings;

public class MavenDependencyResolver implements DependencyResolver
{
   private final MavenContainer container = new MavenContainer();
   private ResourceFactory resourceFactory;

   public MavenDependencyResolver()
   {
   }

   public MavenDependencyResolver(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Set<Dependency> resolveDependencies(DependencyQuery query)
   {
      Set<Dependency> result = new HashSet<>();
      Predicate<Dependency> filter = query.getDependencyFilter();
      RepositorySystem system = container.getRepositorySystem();
      Settings settings = container.getSettings();

      DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);

      Artifact queryArtifact = MavenConvertUtils.coordinateToMavenArtifact(query.getCoordinate());

      List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
               settings);
      remoteRepos.addAll(MavenRepositories.getRemoteRepositories(container, settings));

      CollectRequest collectRequest = new CollectRequest(new org.eclipse.aether.graph.Dependency(queryArtifact,
               query.getScopeType()), remoteRepos);

      DependencyRequest request = new DependencyRequest(collectRequest, null);

      DependencyResult artifacts;
      try
      {
         artifacts = system.resolveDependencies(session, request);
      }
      catch (NullPointerException e)
      {
         throw new RuntimeException("Could not resolve dependencies from Query [" + query
                  + "] due to underlying exception", e);
      }
      catch (DependencyResolutionException e)
      {
         throw new RuntimeException(e);
      }
      DependencyNode root = artifacts.getRoot();
      ResourceFactory factory = getResourceFactory();
      for (DependencyNode node : root.getChildren())
      {
         Dependency d = MavenConvertUtils.convertToDependency(factory, node);
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
      List<Coordinate> result = new ArrayList<>();
      Predicate<Dependency> filter = query.getDependencyFilter();
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
   VersionRangeResult getVersions(DependencyQuery query)
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

         RepositorySystem maven = container.getRepositorySystem();
         Settings settings = container.getSettings();

         DefaultRepositorySystemSession session = container.setupRepoSession(maven, settings);
         Artifact artifact = MavenConvertUtils.coordinateToMavenArtifact(dep);
         List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
                  settings);
         remoteRepos.addAll(MavenRepositories.getRemoteRepositories(container, settings));

         VersionRangeRequest rangeRequest = new VersionRangeRequest(artifact, remoteRepos, null);

         VersionRangeResult rangeResult = maven.resolveVersionRange(session, rangeRequest);
         return rangeResult;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to look up versions for [" + dep + "]", e);
      }
   }

   @Override
   public Dependency resolveArtifact(DependencyQuery query)
   {
      RepositorySystem system = container.getRepositorySystem();
      Settings settings = container.getSettings();

      List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
               settings);
      remoteRepos.addAll(MavenRepositories.getRemoteRepositories(container, settings));

      DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);
      Artifact queryArtifact = MavenConvertUtils.coordinateToMavenArtifact(query.getCoordinate());
      ArtifactRequest request = new ArtifactRequest(queryArtifact, remoteRepos, null);
      try
      {
         ArtifactResult resolvedArtifact = system.resolveArtifact(session, request);
         Artifact artifact = resolvedArtifact.getArtifact();

         @SuppressWarnings("unchecked")
         FileResource<?> artifactResource = getResourceFactory().create(FileResource.class, artifact.getFile());

         return DependencyBuilder.create()
                  .setArtifact(artifactResource)
                  .setGroupId(artifact.getGroupId())
                  .setArtifactId(artifact.getArtifactId())
                  .setClassifier(artifact.getClassifier())
                  .setPackaging(artifact.getExtension())
                  .setVersion(artifact.getBaseVersion());
      }
      catch (ArtifactResolutionException e)
      {
         throw new MavenOperationException(e);
      }
   }

   @Override
   public org.jboss.forge.addon.dependencies.DependencyNode resolveDependencyHierarchy(final DependencyQuery query)
   {
      try
      {
         RepositorySystem system = container.getRepositorySystem();
         Settings settings = container.getSettings();
         DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);
         session.setDependencyTraverser(new DependencyTraverser()
         {
            @Override
            public boolean traverseDependency(org.eclipse.aether.graph.Dependency dependency)
            {
               if (query.getScopeType() != null)
                  return query.getScopeType().equals(dependency.getScope());
               else
                  return !"test".equals(dependency.getScope());
            }

            @Override
            public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context)
            {
               return this;
            }
         });
         session.setDependencySelector(new ScopeDependencySelector("test"));

         final CoordinateBuilder coord = CoordinateBuilder.create(query.getCoordinate());
         Artifact queryArtifact = MavenConvertUtils.coordinateToMavenArtifact(coord);

         List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
                  settings);
         remoteRepos.addAll(MavenRepositories.getRemoteRepositories(container, settings));
         CollectRequest collectRequest = new CollectRequest(new org.eclipse.aether.graph.Dependency(queryArtifact,
                  null), remoteRepos);

         DependencyRequest dr = new DependencyRequest(collectRequest, null);

         DependencyResult result = system.resolveDependencies(session, dr);
         DependencyNodeBuilder hierarchy = MavenConvertUtils.toDependencyNode(getResourceFactory(), null,
                  result.getRoot());
         return hierarchy;
      }
      catch (Exception e)
      {
         throw new DependencyException("Could not resolve dependencies for addon [" + query.getCoordinate() + "]", e);
      }
   }

   @Override
   public DependencyMetadata resolveDependencyMetadata(final DependencyQuery query)
   {
      try
      {
         if (Strings.isNullOrEmpty(query.getCoordinate().getVersion()))
         {
            throw new IllegalArgumentException("Dependency query coordinate version must be specified.");
         }

         RepositorySystem system = container.getRepositorySystem();
         Settings settings = container.getSettings();

         DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);
         Artifact artifact = MavenConvertUtils.coordinateToMavenArtifact(query.getCoordinate());

         List<RemoteRepository> remoteRepos = MavenConvertUtils.convertToMavenRepos(query.getDependencyRepositories(),
                  settings);
         remoteRepos.addAll(MavenRepositories.getRemoteRepositories(container, settings));
         ArtifactDescriptorRequest ar = new ArtifactDescriptorRequest(artifact, remoteRepos, null);
         ArtifactDescriptorResult results = system.readArtifactDescriptor(session, ar);

         Artifact a = results.getArtifact();
         Dependency d = DependencyBuilder.create().setArtifactId(a.getArtifactId()).setGroupId(a.getGroupId())
                  .setVersion(a.getBaseVersion());

         return new DependencyMetadataImpl(d, results);
      }
      catch (Exception e)
      {
         throw new DependencyException("Unable to resolve any artifacts for query [" + query + "]", e);
      }
   }

   private ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      }
      return resourceFactory;
   }
}
