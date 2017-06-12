/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

/**
 * Resolves an artifact even from remote repository during resolution of the model.
 *
 * The repositories are added to the resolution chain as found during processing of the POM file. Repository is added
 * only if there is no other repository with same id already defined.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
@SuppressWarnings("deprecation")
public class MavenModelResolver implements ModelResolver
{

   private final List<RemoteRepository> repositories;
   private final Set<String> repositoryIds;

   private final RepositorySystem system;
   private final RepositorySystemSession session;

   /**
    * Creates a new Maven repository resolver. This resolver uses service available to Maven to create an artifact
    * resolution chain
    *
    * @param system the Maven based implementation of the {@link RepositorySystem}
    * @param session the current Maven execution session
    * @param remoteRepositories the list of available Maven repositories
    */
   public MavenModelResolver(RepositorySystem system, RepositorySystemSession session,
            List<RemoteRepository> remoteRepositories)
   {
      this.system = system;
      this.session = session;

      // RemoteRepository is mutable
      this.repositories = new ArrayList<>(remoteRepositories.size());
      for (final RemoteRepository remoteRepository : remoteRepositories)
      {
         this.repositories.add(new RemoteRepository.Builder(remoteRepository).build());
      }

      this.repositoryIds = new HashSet<>(repositories.size());

      for (final RemoteRepository repository : repositories)
      {
         repositoryIds.add(repository.getId());
      }
   }

   /**
    * Cloning constructor
    *
    * @param origin
    */
   private MavenModelResolver(MavenModelResolver origin)
   {
      this(origin.system, origin.session, origin.repositories);
   }

   /*
    * (non-Javadoc)
    *
    * @see org.apache.maven.model.resolution.ModelResolver#addRepository(org.apache.maven.model.Repository)
    */
   @Override
   public void addRepository(Repository repository) throws InvalidRepositoryException
   {
      addRepository(repository, false);
   }

   /*
    * (non-Javadoc)
    *
    * @see org.apache.maven.model.resolution.ModelResolver#newCopy()
    */
   @Override
   public ModelResolver newCopy()
   {
      return new MavenModelResolver(this);
   }

   /*
    * (non-Javadoc)
    *
    * @see org.apache.maven.model.resolution.ModelResolver#resolveModel(java.lang.String, java.lang.String,
    * java.lang.String)
    */
   @Override
   public ModelSource resolveModel(String groupId, String artifactId, String version)
            throws UnresolvableModelException
   {
      Artifact pomArtifact = new DefaultArtifact(groupId, artifactId, "", "pom", version);
      try
      {
         final ArtifactRequest request = new ArtifactRequest(pomArtifact, repositories, null);
         pomArtifact = system.resolveArtifact(session, request).getArtifact();

      }
      catch (ArtifactResolutionException e)
      {
         throw new UnresolvableModelException("Failed to resolve POM for " + groupId + ":" + artifactId + ":"
                  + version + " due to " + e.getMessage(), groupId, artifactId, version, e);
      }

      final File pomFile = pomArtifact.getFile();

      return new FileModelSource(pomFile);

   }

   @Override
   public ModelSource resolveModel(Parent parent) throws UnresolvableModelException
   {

      Artifact artifact = new DefaultArtifact(parent.getGroupId(), parent.getArtifactId(), "", "pom",
               parent.getVersion());

      VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifact, repositories, null);

      try
      {
         VersionRangeResult versionRangeResult = system.resolveVersionRange(session, versionRangeRequest);

         if (versionRangeResult.getHighestVersion() == null)
         {
            throw new UnresolvableModelException("No versions matched the requested range '" + parent.getVersion()
                     + "'", parent.getGroupId(), parent.getArtifactId(),
                     parent.getVersion());

         }

         if (versionRangeResult.getVersionConstraint() != null
                  && versionRangeResult.getVersionConstraint().getRange() != null
                  && versionRangeResult.getVersionConstraint().getRange().getUpperBound() == null)
         {
            throw new UnresolvableModelException("The requested version range '" + parent.getVersion()
                     + "' does not specify an upper bound", parent.getGroupId(),
                     parent.getArtifactId(), parent.getVersion());

         }

         parent.setVersion(versionRangeResult.getHighestVersion().toString());
      }
      catch (VersionRangeResolutionException e)
      {
         throw new UnresolvableModelException(e.getMessage(), parent.getGroupId(), parent.getArtifactId(),
                  parent.getVersion(), e);
      }

      return resolveModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
   }

   @Override
   public ModelSource resolveModel(Dependency dep) throws UnresolvableModelException
   {
      return resolveModel(dep.getGroupId(), dep.getArtifactId(), dep.getVersion());
   }

   @Override
   public void addRepository(Repository repository, boolean replace) throws InvalidRepositoryException
   {

      if (session.isIgnoreArtifactDescriptorRepositories())
      {
         return;
      }

      if (!repositoryIds.add(repository.getId()))
      {
         if (!replace)
         {
            return;
         }

         removeMatchingRepository(repositories, repository.getId());
      }

      repositories.add(
               new RemoteRepository.Builder(repository.getId(), repository.getLayout(), repository.getUrl()).build());
   }

   private static void removeMatchingRepository(Iterable<RemoteRepository> repositories, final String id)
   {
      Iterator<RemoteRepository> iterator = repositories.iterator();
      while (iterator.hasNext())
      {
         RemoteRepository remoteRepository = iterator.next();
         if (remoteRepository.getId().equals(id))
         {
            iterator.remove();
         }
      }
   }
}
