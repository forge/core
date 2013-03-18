/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.RepositoryUtils;
import org.jboss.forge.maven.facets.MavenContainer;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyMetadata;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolverProvider;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.ArtifactRepository;
import org.sonatype.aether.repository.LocalArtifactRequest;
import org.sonatype.aether.repository.LocalArtifactResult;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class RepositoryLookup implements DependencyResolverProvider
{
   private MavenContainer container;
   private ResourceFactory factory;
   private ForgeEnvironment environment;

   public RepositoryLookup()
   {
   }

   @Inject
   public RepositoryLookup(final MavenContainer container, final ResourceFactory factory,
            final ForgeEnvironment environment)
   {
      this.container = container;
      this.factory = factory;
      this.environment = environment;
   }

   @Override
   public List<DependencyResource> resolveArtifacts(final Dependency query)
   {
      return resolveArtifacts(query, new ArrayList<DependencyRepository>());
   }

   @Override
   public List<DependencyResource> resolveArtifacts(final Dependency query, final DependencyRepository repository)
   {
      return resolveArtifacts(query, Arrays.asList(repository));
   }

   @Override
   public List<DependencyResource> resolveArtifacts(final Dependency dep, final List<DependencyRepository> repositories)
   {
      List<DependencyResource> result = new ArrayList<DependencyResource>();

      RepositorySystem system = container.lookup(RepositorySystem.class);

      /**
       * First try resolving the artifact directly from the local repository - then fall back to aether. This may be a
       * bad practice but we can revisit if problems arise.
       */
      if (dep.getVersion() != null)
      {
         DirectoryResource dir = (DirectoryResource) factory.getResourceFrom(new File(container.getSettings()
                  .getLocalRepository()));
         if ((dir != null) && dir.exists())
         {
            List<String> segments = new ArrayList<String>();
            segments.addAll(Arrays.asList((dep.getGroupId() + "." + dep.getArtifactId()).split("\\.")));
            segments.add(dep.getVersion());

            for (String seg : segments)
            {
               dir = dir.getChildDirectory(seg);
               if (!dir.isDirectory())
               {
                  break;
               }
            }

            if (dir.isDirectory())
            {
               Resource<?> jar = dir.getChild(dep.getArtifactId() + "-" + dep.getVersion() + "."
                        + dep.getPackagingType());
               if (jar.exists())
               {
                  FileResource<?> jarFile = jar.reify(FileResource.class);
                  result.add(new DependencyResource(jarFile.getResourceFactory(), jarFile
                           .getUnderlyingResourceObject(), dep));
               }
            }
         }
      }

      if (result.isEmpty())
      {
         MavenRepositorySystemSession session = setupRepoSession(system);

         session.setIgnoreInvalidArtifactDescriptor(true);
         session.setIgnoreMissingArtifactDescriptor(true);

         VersionRangeResult versions = getVersions(dep, convertToMavenRepos(repositories));

         VERSION: for (Version version : versions.getVersions())
         {
            ArtifactRepository ar = versions.getRepository(version);
            DependencyBuilder currentVersion = DependencyBuilder.create(dep).setVersion(version.toString());
            Artifact artifact = dependencyToMavenArtifact(currentVersion);

            if (ar instanceof LocalRepository)
            {
               LocalArtifactRequest request = new LocalArtifactRequest(artifact, null, null);
               LocalArtifactResult a = session.getLocalRepositoryManager().find(session, request);

               File file = a.getFile();
               DependencyResource resource = new DependencyResource(factory, file, currentVersion);
               if (!result.contains(resource))
               {
                  result.add(resource);
                  continue VERSION;
               }
            }
            if (ar instanceof RemoteRepository)
            {
               ArtifactRequest request = new ArtifactRequest();
               RemoteRepository remoteRepo = new RemoteRepository(ar.getId(), ar.getContentType(),
                        ((RemoteRepository) ar).getUrl());
               request.addRepository(remoteRepo);
               request.setArtifact(artifact);

               try
               {
                  ArtifactResult a = system.resolveArtifact(session, request);

                  File file = a.getArtifact().getFile();
                  DependencyResource resource = new DependencyResource(factory, file, currentVersion);
                  if (!result.contains(resource))
                  {
                     result.add(resource);
                     continue VERSION;
                  }
               }
               catch (ArtifactResolutionException e)
               {
                  System.out.println(e.getMessage());
               }
            }
         }
      }
      return result;
   }

   @Override
   public List<DependencyResource> resolveDependencies(final Dependency query)
   {
      return resolveDependencies(query, new ArrayList<DependencyRepository>());
   }

   @Override
   public List<DependencyResource> resolveDependencies(final Dependency query, final DependencyRepository repository)
   {
      return resolveDependencies(query, Arrays.asList(repository));
   }

   @Override
   public List<DependencyResource> resolveDependencies(Dependency dep, final List<DependencyRepository> repositories)
   {
      List<DependencyResource> result = new ArrayList<DependencyResource>();

      try
      {
         if (Strings.isNullOrEmpty(dep.getVersion()))
         {
            dep = DependencyBuilder.create(dep).setVersion("[,)");
         }

         RepositorySystem system = container.lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(system);

         Artifact artifact = dependencyToMavenArtifact(dep);
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(artifact, null),
                  convertToMavenRepos(repositories));
         DependencyRequest request = new DependencyRequest(collectRequest, null);

         DependencyResult artifacts = system.resolveDependencies(session, request);

         for (ArtifactResult a : artifacts.getArtifactResults())
         {
            File file = a.getArtifact().getFile();
            Dependency d = DependencyBuilder.create().setArtifactId(a.getArtifact().getArtifactId())
                     .setGroupId(a.getArtifact().getGroupId()).setVersion(a.getArtifact().getBaseVersion())
                     .setPackagingType(a.getArtifact().getExtension());
            DependencyResource resource = new DependencyResource(factory, file, d);
            result.add(resource);
         }
         return result;
      }
      catch (Exception e)
      {
         throw new ProjectModelException("Unable to resolve an artifact", e);
      }
   }

   @Override
   public DependencyMetadata resolveDependencyMetadata(final Dependency query)
   {
      return resolveDependencyMetadata(query, new ArrayList<DependencyRepository>());
   }

   @Override
   public DependencyMetadata resolveDependencyMetadata(final Dependency query, final DependencyRepository repository)
   {
      return resolveDependencyMetadata(query, Arrays.asList(repository));
   }

   @Override
   public DependencyMetadata resolveDependencyMetadata(Dependency query, final List<DependencyRepository> repositories)
   {
      try
      {
         if (Strings.isNullOrEmpty(query.getVersion()))
         {
            query = DependencyBuilder.create(query).setVersion("[,)");
         }

         RepositorySystem system = container.lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(system);

         Artifact artifact = dependencyToMavenArtifact(query);

         ArtifactDescriptorRequest ar = new ArtifactDescriptorRequest(artifact, convertToMavenRepos(repositories), null);
         ArtifactDescriptorResult results = system.readArtifactDescriptor(session, ar);

         Artifact a = results.getArtifact();
         Dependency d = DependencyBuilder.create().setArtifactId(a.getArtifactId()).setGroupId(a.getGroupId())
                  .setVersion(a.getBaseVersion());

         return new DependencyMetadataImpl(d, results);
      }
      catch (Exception e)
      {
         throw new ProjectModelException("Unable to resolve any artifacts for query [" + query + "]", e);
      }
   }

   @Override
   public List<Dependency> resolveVersions(final Dependency query)
   {
      return resolveVersions(query, new ArrayList<DependencyRepository>());
   }

   @Override
   public List<Dependency> resolveVersions(final Dependency query, final DependencyRepository repository)
   {
      return resolveVersions(query, Arrays.asList(repository));
   }

   @Override
   public List<Dependency> resolveVersions(final Dependency dep, final List<DependencyRepository> repositories)
   {
      List<Dependency> result = new ArrayList<Dependency>();

      List<RemoteRepository> remoteRepos = convertToMavenRepos(repositories);
      VersionRangeResult r = getVersions(dep, remoteRepos);

      for (Version v : r.getVersions())
      {
         result.add(DependencyBuilder.create(dep).setVersion(v.toString()));
      }

      return result;
   }

   private MavenRepositorySystemSession setupRepoSession(final RepositorySystem repoSystem)
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setOffline(!environment.isOnline());
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
         remoteRepository.setProxy(RepositoryUtils.convertFromMavenProxy(activeProxy));
      }
      return remoteRepository;
   }

   private List<RemoteRepository> convertToMavenRepos(final List<DependencyRepository> repositories)
   {
      List<DependencyRepository> temp = new ArrayList<DependencyRepository>();
      temp.addAll(repositories);

      List<RemoteRepository> remoteRepos = new ArrayList<RemoteRepository>();
      boolean hasCentral = false;
      for (DependencyRepository deprep : temp)
      {
         remoteRepos.add(convertToMavenRepo(deprep));
         if (KnownRepository.CENTRAL.getUrl().equals(deprep.getUrl()))
         {
            hasCentral = true;
         }
      }
      if (!hasCentral)
      {
         RemoteRepository central = convertToMavenRepo(new DependencyRepositoryImpl(KnownRepository.CENTRAL.getId(),
                  KnownRepository.CENTRAL.getUrl()));
         central.setPolicy(true, new RepositoryPolicy().setEnabled(false));
         remoteRepos.add(central);
      }
      return remoteRepos;
   }

   private VersionRangeResult getVersions(Dependency dep, final List<RemoteRepository> repositories)
   {
      try
      {
         String version = dep.getVersion();
         if (Strings.isNullOrEmpty(version))
         {
            dep = DependencyBuilder.create(dep).setVersion("[,)");
         }
         else if (!version.matches("(\\(|\\[).*?(\\)|\\])"))
         {
            dep = DependencyBuilder.create(dep).setVersion("[" + version + "]");
         }

         RepositorySystem maven = container.lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(maven);
         session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_ALWAYS);

         Artifact artifact = dependencyToMavenArtifact(dep);
         VersionRangeRequest rangeRequest = new VersionRangeRequest(artifact, repositories, null);

         VersionRangeResult rangeResult = maven.resolveVersionRange(session, rangeRequest);
         return rangeResult;
      }
      catch (Exception e)
      {
         throw new ProjectModelException("Failed to look up versions for [" + dep + "]", e);
      }
   }

   public Artifact dependencyToMavenArtifact(final Dependency dep)
   {
      Artifact artifact = new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getClassifier(),
               dep.getPackagingType() == null ? "jar" : dep.getPackagingType(), dep.getVersion());
      return artifact;
   }
}