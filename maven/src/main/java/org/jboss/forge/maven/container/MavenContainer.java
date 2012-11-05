/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.repository.layout.FlatRepositoryLayout;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 * Configures the Maven API for usage inside Forge
 */
@ApplicationScoped
public class MavenContainer
{
   private static final String M2_HOME = System.getenv().get("M2_HOME");

   private DefaultPlexusContainer container = null;

   public ProjectBuildingRequest getRequest()
   {
      boolean online = true;
      // TODO: Online comes from ForgeEnvironment
      return getBuildingRequest(online == false);
   }

   public ProjectBuildingRequest getOfflineRequest()
   {
      return getBuildingRequest(true);
   }

   public ProjectBuildingRequest getBuildingRequest(final boolean offline)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      try
      {
         Settings settings = getSettings();
         // TODO this needs to be configurable via .forge
         // TODO this reference to the M2_REPO should probably be centralized

         MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
         lookup(MavenExecutionRequestPopulator.class).populateFromSettings(executionRequest, getSettings());
         ProjectBuildingRequest request = executionRequest.getProjectBuildingRequest();

         ArtifactRepository localRepository = new MavenArtifactRepository(
                  "local", new File(settings.getLocalRepository()).toURI().toURL().toString(),
                  getContainer().lookup(ArtifactRepositoryLayout.class),
                  new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                           ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN),
                  new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                           ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN));
         request.setLocalRepository(localRepository);

         List<ArtifactRepository> settingsRepos = getEnabledRepositories(settings);

         request.setRemoteRepositories(settingsRepos);
         request.setSystemProperties(System.getProperties());

         MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
         Proxy activeProxy = settings.getActiveProxy();
         if (activeProxy != null)
         {
            DefaultProxySelector dps = new DefaultProxySelector();
            dps.add(convertFromMavenProxy(activeProxy), activeProxy.getNonProxyHosts());
            repositorySession.setProxySelector(dps);
         }
         repositorySession.setLocalRepositoryManager(new SimpleLocalRepositoryManager(settings.getLocalRepository()));
         repositorySession.setOffline(offline);

         request.setRepositorySession(repositorySession);
         request.setProcessPlugins(false);
         // request.setPluginArtifactRepositories(Arrays.asList(localRepository));
         request.setResolveDependencies(false);
         return request;
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

   List<ArtifactRepository> getEnabledRepositories(Settings settings)
   {
      List<ArtifactRepository> settingsRepos = new ArrayList<ArtifactRepository>();
      List<String> activeProfiles = settings.getActiveProfiles();

      Map<String, Profile> profiles = settings.getProfilesAsMap();

      for (String id : activeProfiles)
      {
         Profile profile = profiles.get(id);
         List<Repository> repositories = profile.getRepositories();
         for (Repository repository : repositories)
         {
            settingsRepos.add(convertFromMavenSettingsRepository(repository));
         }
      }
      return settingsRepos;
   }

   public Settings getSettings()
   {
      try
      {
         SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
         SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
         settingsRequest
                  .setUserSettingsFile(new File(getUserHomePath() + "/.m2/settings.xml"));

         if (M2_HOME != null)
            settingsRequest.setGlobalSettingsFile(new File(M2_HOME + "/conf/settings.xml"));

         SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsRequest);
         Settings effectiveSettings = settingsBuildingResult.getEffectiveSettings();

         if (effectiveSettings.getLocalRepository() == null)
         {
            effectiveSettings.setLocalRepository(getUserHomePath() + "/.m2/repository");
         }

         return effectiveSettings;
      }
      catch (SettingsBuildingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public <T> T lookup(Class<T> type)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      try
      {
         return getContainer().lookup(type);
      }
      catch (ComponentLookupException e)
      {
         throw new RuntimeException("Could not look up component of type [" + type.getName() + "]", e);
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

   private DefaultPlexusContainer getContainer()
   {
      if (container == null)
      {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         try
         {
            container = new DefaultPlexusContainer();
            ConsoleLoggerManager loggerManager = new ConsoleLoggerManager();
            loggerManager.setThreshold("ERROR");
            container.setLoggerManager(loggerManager);
         }
         catch (Exception e)
         {
            throw new RuntimeException(
                     "Could not initialize Maven", e);
         }
         finally
         {
            /*
             * We reset the classloader to prevent potential modules bugs if Classwords container changes classloaders
             * on us
             */
            Thread.currentThread().setContextClassLoader(cl);
         }
      }
      return container;
   }

   public static org.sonatype.aether.repository.Proxy convertFromMavenProxy(org.apache.maven.settings.Proxy proxy)
   {
      org.sonatype.aether.repository.Proxy result = null;
      if (proxy != null)
      {
         Authentication auth = new Authentication(proxy.getUsername(), proxy.getPassword());
         result = new org.sonatype.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), auth);
      }
      return result;
   }

   public static ArtifactRepository convertFromMavenSettingsRepository(Repository repository)
   {
      MavenArtifactRepository result = new MavenArtifactRepository();
      result.setId(repository.getId());
      result.setUrl(repository.getUrl());

      String layout = repository.getLayout();
      if ("default".equals(layout))
         result.setLayout(new DefaultRepositoryLayout());
      else if ("flat".equals(layout))
         result.setLayout(new FlatRepositoryLayout());

      RepositoryPolicy releases = repository.getReleases();
      if (releases != null)
         result.setReleaseUpdatePolicy(new ArtifactRepositoryPolicy(releases.isEnabled(), releases.getUpdatePolicy(),
                  releases.getChecksumPolicy()));

      RepositoryPolicy snapshots = repository.getSnapshots();
      if (snapshots != null)
         result.setSnapshotUpdatePolicy(new ArtifactRepositoryPolicy(snapshots.isEnabled(),
                  snapshots.getUpdatePolicy(),
                  snapshots.getChecksumPolicy()));

      return result;
   }

   private File getUserHomeDir()
   {
      return new File(System.getProperty("user.home")).getAbsoluteFile();
   }

   private String getUserHomePath()
   {
      return getUserHomeDir().getAbsolutePath();
   }
}