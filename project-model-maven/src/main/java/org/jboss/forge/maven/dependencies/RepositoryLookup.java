package org.jboss.forge.maven.dependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.jboss.forge.ForgeEnvironment;
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
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.repository.ArtifactRepository;
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

   // public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";
   // public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

   // public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

   // private static final String DEFAULT_USER_SETTINGS_PATH = OSUtils.getUserHomePath().concat( "/.m2/settings.xml");
   // private static final String DEFAULT_REPOSITORY_PATH = OSUtils.getUserHomePath().concat( "/.m2/repository");

   // public static void setRemoteRepository()
   // {
   // System.setProperty(ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
   // System.setProperty(ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   // }

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

      try
      {
         RepositorySystem system = container.getContainer().lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(system);

         session.setIgnoreInvalidArtifactDescriptor(true);
         session.setIgnoreMissingArtifactDescriptor(true);

         VersionRangeResult versions = getVersions(dep, convertToMavenRepos(repositories));

         VERSION: for (Version version : versions.getVersions())
         {
            ArtifactRequest request = new ArtifactRequest();
            ArtifactRepository ar = versions.getRepository(version);
            if (ar instanceof RemoteRepository)
            {
               RemoteRepository remoteRepo = new RemoteRepository(ar.getId(), ar.getContentType(),
                           ((RemoteRepository) ar).getUrl());
               request.addRepository(remoteRepo);
               DependencyBuilder currentVersion = DependencyBuilder.create(dep).setVersion(version.toString());
               request.setArtifact(new DefaultArtifact(currentVersion.toCoordinates()));

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
      catch (ComponentLookupException e)
      {
         throw new ProjectModelException("Error in dependency resolution container", e);
      }
      return result;
   }

   @Override
   public List<DependencyResource> resolveDependencies(final Dependency query)
   {
      return resolveDependencies(query, new ArrayList<DependencyRepository>());
   }

   @Override
   public List<DependencyResource> resolveDependencies(final Dependency query,
            final DependencyRepository repository)
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

         RepositorySystem system = container.getContainer().lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(system);

         DefaultArtifact artifact = new DefaultArtifact(dep.toCoordinates());
         CollectRequest collectRequest = new CollectRequest(new org.sonatype.aether.graph.Dependency(
                  artifact, null), convertToMavenRepos(repositories));
         DependencyRequest request = new DependencyRequest(collectRequest, null);

         DependencyResult artifacts = system.resolveDependencies(session, request);

         for (ArtifactResult a : artifacts.getArtifactResults())
         {
            File file = a.getArtifact().getFile();
            Dependency d = DependencyBuilder.create().setArtifactId(a.getArtifact().getArtifactId())
                     .setGroupId(a.getArtifact().getGroupId())
                     .setVersion(a.getArtifact().getVersion());
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
   public DependencyMetadata resolveDependencyMetadata(Dependency query,
            final List<DependencyRepository> repositories)
   {
      try
      {
         if (Strings.isNullOrEmpty(query.getVersion()))
         {
            query = DependencyBuilder.create(query).setVersion("[,)");
         }

         RepositorySystem system = container.getContainer().lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(system);

         DefaultArtifact artifact = new DefaultArtifact(query.toCoordinates());

         ArtifactDescriptorRequest ar = new ArtifactDescriptorRequest(artifact, convertToMavenRepos(repositories), null);
         ArtifactDescriptorResult results = system.readArtifactDescriptor(session, ar);

         Artifact a = results.getArtifact();
         Dependency d = DependencyBuilder.create().setArtifactId(a.getArtifactId())
                     .setGroupId(a.getGroupId())
                     .setVersion(a.getVersion());

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

      LocalRepository localRepo = new LocalRepository(container.getSettings().getLocalRepository());
      session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(localRepo));

      session.setTransferErrorCachingEnabled(false);
      session.setNotFoundCachingEnabled(false);
      return session;
   }

   private RemoteRepository convertToMavenRepo(final DependencyRepository repo)
   {
      return new RemoteRepository(repo.getId(), "default", repo.getUrl());
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

         RepositorySystem maven = container.getContainer().lookup(RepositorySystem.class);
         MavenRepositorySystemSession session = setupRepoSession(maven);

         VersionRangeRequest rangeRequest = new VersionRangeRequest(new DefaultArtifact(dep.toCoordinates()),
                  repositories, null);

         VersionRangeResult rangeResult = maven.resolveVersionRange(session, rangeRequest);
         return rangeResult;
      }
      catch (Exception e)
      {
         throw new ProjectModelException("Failed to look up versions for [" + dep + "]", e);
      }
   }

}