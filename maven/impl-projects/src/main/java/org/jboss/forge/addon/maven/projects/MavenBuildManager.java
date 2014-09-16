/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
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
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;
import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.maven.environment.Network;
import org.jboss.forge.addon.maven.projects.util.RepositoryUtils;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.monitor.ResourceListener;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.util.Assert;

/**
 * Manages maven builds based on a {@link MavenModelResource}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class MavenBuildManager
{
   // TODO: Replace this with a Cache implementation
   private Map<MavenModelResource, ProjectBuildingResult> cache = new WeakHashMap<>();

   @Inject
   private PlexusContainer plexus;

   @Inject
   private MavenContainer container;

   @Inject
   private Environment environment;

   private ProjectBuilder builder;

   ProjectBuildingResult getProjectBuildingResult(MavenModelResource pomResource) throws ProjectBuildingException
   {
      ProjectBuildingResult result = cache.get(pomResource);
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
               result = getBuilder().build(new FileResourceModelSource(pomResource), request);
               // If under a transaction, don't start monitoring
            }
            else
            {
               result = getBuilder().build(pomResource.getUnderlyingResourceObject(), request);
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
            if (result != null)
               cache.put(pomResource, result);
         }
      }
      return result;
   }

   private void monitorResource(final MavenModelResource pomResource)
   {
      final ResourceMonitor monitor = pomResource.monitor();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            cache.remove(pomResource);
            monitor.cancel();
         }
      });
   }

   ProjectBuildingRequest getProjectBuildingRequest()
   {
      return getProjectBuildingRequest(Network.isOffline(environment));
   }

   ProjectBuildingRequest getProjectBuildingRequest(final boolean offline)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      try
      {
         Settings settings = container.getSettings();
         // TODO this needs to be configurable via .forge
         // TODO this reference to the M2_REPO should probably be centralized

         MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
         MavenExecutionRequestPopulator populator = plexus.lookup(MavenExecutionRequestPopulator.class);
         populator.populateFromSettings(executionRequest, container.getSettings());
         populator.populateDefaults(executionRequest);
         RepositorySystem system = plexus.lookup(RepositorySystem.class);
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

   private ProjectBuilder getBuilder()
   {
      if (builder == null)
         builder = plexus.lookup(ProjectBuilder.class);
      return builder;
   }

   File getLocalRepositoryDirectory()
   {
      return new File(container.getSettings().getLocalRepository()).getAbsoluteFile();
   }

   void evictFromCache(MavenModelResource pom)
   {
      cache.remove(pom);
   }

}