/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.dependencies;

import static org.jboss.forge.addon.maven.dependencies.MavenConvertUtils.convertToMavenRepos;
import static org.jboss.forge.addon.maven.dependencies.MavenConvertUtils.coordinateToMavenArtifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Settings;
import org.jboss.forge.addon.dependencies.AddonDependencyResolver;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyException;
import org.jboss.forge.addon.dependencies.DependencyMetadata;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyNodeBuilder;
import org.jboss.forge.addon.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.resolver.impl.maven.logging.LogTransferListener;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionContext;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;
import org.sonatype.aether.version.Version;

@Exported
public class MavenDependencyResolver implements DependencyResolver, AddonDependencyResolver
{
   private final MavenContainer container;
   private final ResourceFactory factory;

   public MavenDependencyResolver()
   {
      super();
      this.container = new MavenContainer();
      this.factory = new FileResourceFactory();
   }

   @Inject
   public MavenDependencyResolver(ResourceFactory factory, MavenContainer container)
   {
      super();
      this.container = container;
      this.factory = factory;
   }

   @Override
   public Set<Dependency> resolveDependencies(DependencyQuery query)
   {
      Set<Dependency> result = new HashSet<Dependency>();
      Predicate<Dependency> filter = query.getDependencyFilter();
      RepositorySystem system = container.getRepositorySystem();
      Settings settings = container.getSettings();

      MavenRepositorySystemSession session = container.setupRepoSession(system, settings);

      Artifact queryArtifact = coordinateToMavenArtifact(query.getCoordinate());

      List<RemoteRepository> remoteRepos = getRemoteRepositories(query, settings);

      CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
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
      List<Coordinate> result = new ArrayList<Coordinate>();
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

         MavenRepositorySystemSession session = container.setupRepoSession(maven, settings);

         Artifact artifact = coordinateToMavenArtifact(dep);

         List<RemoteRepository> remoteRepos = getRemoteRepositories(query, settings);

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

      List<RemoteRepository> remoteRepos = getRemoteRepositories(query, settings);

      MavenRepositorySystemSession session = container.setupRepoSession(system, settings);

      Artifact queryArtifact = coordinateToMavenArtifact(query.getCoordinate());
      ArtifactRequest request = new ArtifactRequest(queryArtifact, remoteRepos, null);
      try
      {
         ArtifactResult resolvedArtifact = system.resolveArtifact(session, request);
         Artifact artifact = resolvedArtifact.getArtifact();

         @SuppressWarnings("unchecked")
         FileResource<?> artifactResource = factory.create(FileResource.class, artifact.getFile());

         return DependencyBuilder.create()
                  .setArtifact(artifactResource)
                  .setGroupId(artifact.getGroupId())
                  .setArtifactId(artifact.getArtifactId())
                  .setClassifier(artifact.getClassifier())
                  .setPackaging(artifact.getExtension())
                  .setVersion(artifact.getVersion());
      }
      catch (ArtifactResolutionException e)
      {
         throw new MavenOperationException(e);
      }
   }

   @Override
   public org.jboss.forge.addon.dependencies.DependencyNode resolveAddonDependencyHierarchy(final DependencyQuery query)
   {
      try
      {
         RepositorySystem system = container.getRepositorySystem();
         Settings settings = container.getSettings();
         MavenRepositorySystemSession session = container.setupRepoSession(system, settings);
         session.setTransferListener(new LogTransferListener());

         session.setDependencyTraverser(new DependencyTraverser()
         {
            @Override
            public boolean traverseDependency(org.sonatype.aether.graph.Dependency dependency)
            {
               boolean isForgeAddon = DependencyNodeUtil.FORGE_ADDON_CLASSIFIER.equals(dependency.getArtifact()
                        .getClassifier());
               // We don't want to traverse non-addons optional dependencies
               if (!isForgeAddon && dependency.isOptional())
               {
                  return false;
               }
               boolean result;
               if (query.getScopeType() != null)
               {
                  result = query.getScopeType().equals(dependency.getScope());
               }
               else
               {
                  result = !"test".equals(dependency.getScope());
               }
               return result;
            }

            @Override
            public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context)
            {
               return this;
            }
         });
         session.setDependencySelector(new AddonDependencySelector());

         final CoordinateBuilder coord = CoordinateBuilder.create(query.getCoordinate());
         Artifact queryArtifact = coordinateToMavenArtifact(coord);

         List<RemoteRepository> remoteRepos = getRemoteRepositories(query, settings);
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
                  null), remoteRepos);

         DependencyRequest dr = new DependencyRequest(collectRequest, null);

         DependencyResult result = system.resolveDependencies(session, dr);
         DependencyNodeBuilder hierarchy = MavenConvertUtils.toDependencyNode(factory, null, result.getRoot());
         return hierarchy;
      }
      catch (Exception e)
      {
         throw new DependencyException("Could not resolve dependencies for addon [" + query.getCoordinate() + "]", e);
      }
   }

   @Override
   public org.jboss.forge.addon.dependencies.DependencyNode resolveDependencyHierarchy(final DependencyQuery query)
   {
      try
      {
         RepositorySystem system = container.getRepositorySystem();
         Settings settings = container.getSettings();
         MavenRepositorySystemSession session = container.setupRepoSession(system, settings);
         session.setTransferListener(new LogTransferListener());

         session.setDependencyTraverser(new DependencyTraverser()
         {
            @Override
            public boolean traverseDependency(org.sonatype.aether.graph.Dependency dependency)
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
         Artifact queryArtifact = coordinateToMavenArtifact(coord);

         List<RemoteRepository> remoteRepos = getRemoteRepositories(query, settings);
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(queryArtifact,
                  null), remoteRepos);

         DependencyRequest dr = new DependencyRequest(collectRequest, null);

         DependencyResult result = system.resolveDependencies(session, dr);
         DependencyNodeBuilder hierarchy = MavenConvertUtils.toDependencyNode(factory, null, result.getRoot());
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
         MavenRepositorySystemSession session = container.setupRepoSession(system, settings);

         Artifact artifact = coordinateToMavenArtifact(query.getCoordinate());

         List<RemoteRepository> mavenRepositories = getRemoteRepositories(query, settings);
         ArtifactDescriptorRequest ar = new ArtifactDescriptorRequest(artifact, mavenRepositories, null);
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

   private List<RemoteRepository> getRemoteRepositories(DependencyQuery query, Settings settings)
   {
      List<RemoteRepository> remoteRepos = convertToMavenRepos(query.getDependencyRepositories(), settings);
      if (remoteRepos.isEmpty())
         remoteRepos = convertToMavenRepos(
                  Arrays.asList(new DependencyRepository("central", "http://repo1.maven.org/maven2")),
                  settings);
      remoteRepos.addAll(container.getEnabledRepositoriesFromProfile(settings));
      return remoteRepos;
   }
}
