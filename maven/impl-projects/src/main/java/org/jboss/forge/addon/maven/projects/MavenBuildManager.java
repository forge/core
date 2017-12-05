/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.maven.environment.Network;
import org.jboss.forge.addon.maven.impl.DefaultModelCache;
import org.jboss.forge.addon.maven.impl.FileResourceModelSource;
import org.jboss.forge.addon.maven.impl.MavenModelResolver;
import org.jboss.forge.addon.maven.profiles.ProfileAdapter;
import org.jboss.forge.addon.maven.projects.util.RepositoryUtils;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.manager.maven.util.MavenRepositories;
import org.jboss.forge.furnace.util.Assert;

/**
 * Manages maven builds based on a {@link MavenModelResource}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenBuildManager
{
   // TODO: Replace this with a decent cache implementation
   private Map<String, ProjectBuildingResult> cacheProject = new WeakHashMap<>();
   private ProjectBuilder projectBuilder;
   private PlexusContainer plexus;

   private Map<String, ModelBuildingResult> cacheModel = new WeakHashMap<>();

   private ModelBuilder modelBuilder;

   private MavenContainer container = new MavenContainer();
   private Environment environment;

   public ModelBuildingResult getModelBuildingResult(MavenModelResource pomResource) throws ModelBuildingException
   {
      ModelBuildingResult result = cacheModel.get(pomResource.getFullyQualifiedName());
      if (result == null)
      {
         ModelBuilder builder = getModelBuilder();
         DefaultModelBuildingRequest request = getModelBuildingRequest();
         boolean inTransaction = !pomResource.getUnderlyingResourceObject().exists();
         // FORGE-1287
         if (inTransaction)
         {
            // If under a transaction, don't start monitoring
            request.setModelSource(new FileResourceModelSource(pomResource));
         }
         else
         {
            request.setPomFile(pomResource.getUnderlyingResourceObject());
            monitorResource(pomResource);
         }
         result = builder.build(request);
         if (!Projects.isCacheDisabled())
         {
            cacheModel.put(pomResource.getFullyQualifiedName(), result);
         }
      }
      return result;
   }

   /***
    * @param pomResource
    * @return
    * @throws ProjectBuildingException
    */
   ProjectBuildingResult getProjectBuildingResult(MavenModelResource pomResource) throws ProjectBuildingException
   {
      ProjectBuildingResult result = cacheProject.get(pomResource.getFullyQualifiedName());
      if (result == null)
      {
         try
         {
            ProjectBuildingRequest request = getProjectBuildingRequest();
            Assert.notNull(request, "Project building request was null");
            request.setResolveDependencies(true);
            boolean inTransaction = !pomResource.getUnderlyingResourceObject().exists();
            // FORGE-1287
            if (inTransaction)
            {
               result = getProjectBuilder().build(new FileResourceModelSource(pomResource), request);
               // If under a transaction, don't start monitoring
            }
            else
            {
               result = getProjectBuilder().build(pomResource.getUnderlyingResourceObject(), request);
               monitorResource(pomResource);
            }
         }
         catch (ProjectBuildingException pbe)
         {
            List<ProjectBuildingResult> results = pbe.getResults();
            if (results != null && results.size() > 0)
               result = results.get(0);
            throw pbe;
         }
         finally
         {
            if (result != null && !Projects.isCacheDisabled())
               cacheProject.put(pomResource.getFullyQualifiedName(), result);
         }
      }
      return result;
   }

   private void monitorResource(final MavenModelResource pomResource)
   {
      if (Projects.isCacheDisabled())
      {
         return;
      }
      final ResourceMonitor monitor = pomResource.monitor();
      monitor.addResourceListener((event) -> {
         evictFromCache(pomResource);
         monitor.cancel();
      });
   }

   /**
    * @deprecated Use {@link #getModelBuildingRequest(MavenModelResource)}
    */
   @Deprecated
   ProjectBuildingRequest getProjectBuildingRequest()
   {
      return getProjectBuildingRequest(Network.isOffline(getEnvironment()));
   }

   private DefaultModelBuildingRequest getModelBuildingRequest()
   {
      Settings settings = container.getSettings();
      RepositorySystem system = container.getRepositorySystem();
      DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);
      List<RemoteRepository> remoteRepositories = MavenRepositories.getRemoteRepositories(container, settings);
      MavenModelResolver resolver = new MavenModelResolver(system, session, remoteRepositories);
      DefaultModelBuildingRequest request = new DefaultModelBuildingRequest()
               .setSystemProperties(System.getProperties())
               .setModelResolver(resolver)
               .setLocationTracking(true)
               .setModelCache(DefaultModelCache.newInstance(session))
               .setProfiles(settings.getProfiles().stream()
                        .map(ProfileAdapter::new)
                        .collect(Collectors.toList()))
               .setActiveProfileIds(settings.getActiveProfiles());
      return request;
   }

   @SuppressWarnings("deprecation")
   private ProjectBuildingRequest getProjectBuildingRequest(final boolean offline)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      try
      {
         Settings settings = container.getSettings();
         // TODO this needs to be configurable via .forge
         // TODO this reference to the M2_REPO should probably be centralized

         MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
         MavenExecutionRequestPopulator populator = getPlexus().lookup(MavenExecutionRequestPopulator.class);
         populator.populateFromSettings(executionRequest, container.getSettings());
         populator.populateDefaults(executionRequest);
         RepositorySystem system = getPlexus().lookup(RepositorySystem.class);
         ProjectBuildingRequest request = executionRequest.getProjectBuildingRequest();

         ArtifactRepository localRepository = RepositoryUtils.toArtifactRepository("local",
                  new File(settings.getLocalRepository()).toURI().toURL().toString(), null, true, true);
         request.setLocalRepository(localRepository);

         List<ArtifactRepository> settingsRepos = new ArrayList<>(request.getRemoteRepositories());
         List<String> activeProfiles = settings.getActiveProfiles();

         Map<String, Profile> profiles = settings.getProfilesAsMap();

         for (String id : activeProfiles)
         {
            Profile profile = profiles.get(id);
            if (profile != null)
            {
               List<Repository> repositories = profile.getRepositories();
               for (Repository repository : repositories)
               {
                  settingsRepos.add(RepositoryUtils.convertFromMavenSettingsRepository(repository));
               }
            }
         }
         request.setRemoteRepositories(settingsRepos);
         request.setSystemProperties(System.getProperties());

         DefaultRepositorySystemSession repositorySession = MavenRepositorySystemUtils.newSession();
         Proxy activeProxy = settings.getActiveProxy();
         if (activeProxy != null)
         {
            DefaultProxySelector dps = new DefaultProxySelector();
            dps.add(RepositoryUtils.convertFromMavenProxy(activeProxy), activeProxy.getNonProxyHosts());
            repositorySession.setProxySelector(dps);
         }
         LocalRepository localRepo = new LocalRepository(settings.getLocalRepository());
         repositorySession.setLocalRepositoryManager(system.newLocalRepositoryManager(repositorySession, localRepo));
         repositorySession.setOffline(offline);
         List<Mirror> mirrors = executionRequest.getMirrors();
         if (mirrors != null)
         {
            DefaultMirrorSelector mirrorSelector = new DefaultMirrorSelector();
            for (Mirror mirror : mirrors)
            {
               mirrorSelector.add(mirror.getId(), mirror.getUrl(), mirror.getLayout(), false, mirror.getMirrorOf(),
                        mirror.getMirrorOfLayouts());
            }
            repositorySession.setMirrorSelector(mirrorSelector);
         }

         request.setRepositorySession(repositorySession);
         request.setProcessPlugins(false);
         request.setResolveDependencies(false);
         return request;
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException(
                  "Could not create Maven project building request", e);
      }
      finally
      {
         /*
          * We reset the classloader to prevent potential modules bugs if Classwords container changes classloaders on
          * us
          */
         Thread.currentThread().setContextClassLoader(cl);
      }
   }

   private ModelBuilder getModelBuilder()
   {
      if (modelBuilder == null)
      {
         modelBuilder = new DefaultModelBuilderFactory().newInstance();
      }
      return modelBuilder;
   }

   private ProjectBuilder getProjectBuilder()
   {
      if (projectBuilder == null)
      {
         projectBuilder = getPlexus().lookup(ProjectBuilder.class);
      }
      return projectBuilder;
   }

   File getLocalRepositoryDirectory()
   {
      return new File(container.getSettings().getLocalRepository()).getAbsoluteFile();
   }

   void evictFromCache(MavenModelResource pom)
   {
      String key = pom.getFullyQualifiedName();
      cacheProject.remove(key);
      cacheModel.remove(key);
   }

   /**
    * @return the plexus
    */
   private PlexusContainer getPlexus()
   {
      if (plexus == null)
         plexus = SimpleContainer.getServices(getClass().getClassLoader(), PlexusContainer.class).get();
      return plexus;
   }

   private Environment getEnvironment()
   {
      if (environment == null)
      {
         environment = SimpleContainer.getServices(getClass().getClassLoader(), Environment.class).get();
      }
      return environment;
   }

}