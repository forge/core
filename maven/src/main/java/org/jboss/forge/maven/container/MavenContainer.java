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

import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
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
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * Configures the Maven API for usage inside Forge
 */
@ApplicationScoped
public class MavenContainer
{
   private static final String M2_HOME = System.getenv().get("M2_HOME");

   private DefaultPlexusContainer container = null;

   public List<RemoteRepository> getEnabledRepositoriesFromProfile(Settings settings)
   {
      List<RemoteRepository> settingsRepos = new ArrayList<RemoteRepository>();
      List<String> activeProfiles = settings.getActiveProfiles();

      Map<String, Profile> profiles = settings.getProfilesAsMap();

      for (String id : activeProfiles)
      {
         Profile profile = profiles.get(id);
         List<Repository> repositories = profile.getRepositories();
         for (Repository repository : repositories)
         {
            settingsRepos.add(new RemoteRepository(repository.getId(), repository.getLayout(), repository.getUrl()));
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

   private File getUserHomeDir()
   {
      return new File(System.getProperty("user.home")).getAbsoluteFile();
   }

   private String getUserHomePath()
   {
      return getUserHomeDir().getAbsolutePath();
   }
}