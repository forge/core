/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.repository.internal.MavenServiceLocator;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.impl.internal.DefaultServiceLocator;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;

/**
 * Configures the Maven API for usage inside Forge
 *
 * TODO: Remove in the future, use the ShrinkWrap Descriptors API ?
 */
public class MavenContainer
{
   private static final String M2_HOME = System.getenv().get("M2_HOME");

   public List<RemoteRepository> getEnabledRepositoriesFromProfile(Settings settings)
   {
      List<RemoteRepository> settingsRepos = new ArrayList<RemoteRepository>();
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
               settingsRepos.add(new RemoteRepository(repository.getId(), repository.getLayout(), repository.getUrl()));
            }
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
                  .setUserSettingsFile(new File(getUserHomeDir(), "/.m2/settings.xml"));

         if (M2_HOME != null)
            settingsRequest.setGlobalSettingsFile(new File(M2_HOME, "/conf/settings.xml"));

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

   public RepositorySystem getRepositorySystem()
   {

      final DefaultServiceLocator locator = new MavenServiceLocator();
      locator.setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
      // Installing Wagon to fetch from HTTP repositories
      locator.setServices(WagonProvider.class, new ManualWagonProvider());
      locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
      final RepositorySystem repositorySystem = locator.getService(RepositorySystem.class);
      return repositorySystem;
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

   private File getUserHomeDir()
   {
      return new File(System.getProperty("user.home")).getAbsoluteFile();
   }

   private String getUserHomePath()
   {
      return getUserHomeDir().getAbsolutePath();
   }

   MavenRepositorySystemSession setupRepoSession(final RepositorySystem repoSystem, final Settings settings)
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setOffline(false);

      LocalRepository localRepo = new LocalRepository(new File(settings.getLocalRepository()), "");
      session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(localRepo));
      session.setTransferErrorCachingEnabled(false);
      session.setNotFoundCachingEnabled(false);
//      session.setWorkspaceReader(new ClasspathWorkspaceReader());
      return session;
   }

}