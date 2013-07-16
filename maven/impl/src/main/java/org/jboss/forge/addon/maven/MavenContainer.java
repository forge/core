/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.util.repository.SimpleResolutionErrorPolicy;
import org.jboss.forge.addon.maven.dependencies.ClasspathWorkspaceReader;

/**
 * Configures the Maven API for usage inside Furnace
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenContainer
{
   private static final String M2_HOME = System.getenv().get("M2_HOME");

   /**
    * Sets an alternate location to Maven user settings.xml configuration
    */
   public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";

   /**
    * Sets an alternate location of Maven global settings.xml configuration
    */
   public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";

   /**
    * Sets an alternate location of Maven local repository
    */
   public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

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
               settingsRepos.add(new RemoteRepository.Builder(repository.getId(), repository.getLayout(), repository
                        .getUrl()).build());
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
         String userSettingsLocation = System.getProperty(ALT_USER_SETTINGS_XML_LOCATION);
         if (userSettingsLocation != null)
         {
            settingsRequest.setUserSettingsFile(new File(userSettingsLocation));
         }
         else
         {
            settingsRequest.setUserSettingsFile(new File(getUserHomeDir(), "/.m2/settings.xml"));
         }
         String globalSettingsLocation = System.getProperty(ALT_GLOBAL_SETTINGS_XML_LOCATION);
         if (globalSettingsLocation != null)
         {
            settingsRequest.setGlobalSettingsFile(new File(globalSettingsLocation));
         }
         else
         {
            if (M2_HOME != null)
            {
               settingsRequest.setGlobalSettingsFile(new File(M2_HOME, "/conf/settings.xml"));
            }
         }
         SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsRequest);
         Settings effectiveSettings = settingsBuildingResult.getEffectiveSettings();

         if (effectiveSettings.getLocalRepository() == null)
         {
            String userRepositoryLocation = System.getProperty(ALT_LOCAL_REPOSITORY_LOCATION);
            if (userRepositoryLocation != null)
            {
               effectiveSettings.setLocalRepository(userRepositoryLocation);
            }
            else
            {
               effectiveSettings.setLocalRepository(getUserHomePath() + "/.m2/repository");
            }
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

      final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
      locator.setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
      // Installing Wagon to fetch from HTTP repositories
      locator.setServices(WagonProvider.class, new ManualWagonProvider());
      locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
      final RepositorySystem repositorySystem = locator.getService(RepositorySystem.class);
      return repositorySystem;
   }

   public static org.eclipse.aether.repository.Proxy convertFromMavenProxy(org.apache.maven.settings.Proxy proxy)
   {
      org.eclipse.aether.repository.Proxy result = null;
      if (proxy != null)
      {
         Authentication auth = new AuthenticationBuilder().addUsername(proxy.getUsername())
                  .addPassword(proxy.getPassword()).build();
         result = new org.eclipse.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), auth);
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

   public DefaultRepositorySystemSession setupRepoSession(final RepositorySystem repoSystem, final Settings settings)
   {
      DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
      session.setOffline(false);

      LocalRepository localRepo = new LocalRepository(new File(settings.getLocalRepository()), "");
      session.setLocalRepositoryManager(repoSystem.newLocalRepositoryManager(session, localRepo));
      session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_IGNORE);
      session.setCache(new DefaultRepositoryCache());
      session.setResolutionErrorPolicy(new SimpleResolutionErrorPolicy(true, true));
      session.setWorkspaceReader(new ClasspathWorkspaceReader());
      return session;
   }

}