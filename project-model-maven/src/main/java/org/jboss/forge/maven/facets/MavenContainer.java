/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.maven.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Proxy;
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
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.RepositoryUtils;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.shell.util.OSUtils;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@ApplicationScoped
public class MavenContainer
{
   private static final String M2_HOME = System.getenv().get("M2_HOME");

   private ProjectBuildingRequest request;
   private DefaultPlexusContainer container = null;
   private ProjectBuilder builder = null;

   @Inject
   private ForgeEnvironment environment;

   public ProjectBuildingRequest getRequest()
   {
      return getBuildingRequest(environment.isOnline() == false);
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
         request = executionRequest.getProjectBuildingRequest();
         ArtifactRepository localRepository = new MavenArtifactRepository(
                  "local", new File(settings.getLocalRepository()).toURI().toURL().toString(),
                  getContainer().lookup(ArtifactRepositoryLayout.class),
                  new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                           ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN),
                  new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                           ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN));
         request.setLocalRepository(localRepository);
         //request.setRemoteRepositories(new ArrayList<ArtifactRepository>());
         request.setSystemProperties(System.getProperties());

         MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
         Proxy activeProxy = settings.getActiveProxy();
         if (activeProxy != null) {
            DefaultProxySelector dps = new DefaultProxySelector();
            dps.add(RepositoryUtils.convertFromMavenProxy(activeProxy), activeProxy.getNonProxyHosts());
            repositorySession.setProxySelector(dps);
         }
         repositorySession.setLocalRepositoryManager(new SimpleLocalRepositoryManager(settings.getLocalRepository()));
         repositorySession.setOffline(offline);

         request.setRepositorySession(repositorySession);
         request.setProcessPlugins(false);
         //request.setPluginArtifactRepositories(Arrays.asList(localRepository));
         request.setResolveDependencies(false);
         return request;
      }
      catch (Exception e)
      {
         throw new ProjectModelException(
                  "Could not create Maven project building request", e);
      }
      finally
      {
         /*
          * We reset the classloader to prevent potential modules bugs 
          * if Classwords container changes classloaders on us
          */
         Thread.currentThread().setContextClassLoader(cl);
      }
   }

   public Settings getSettings()
   {
      try
      {
         SettingsBuilder settingsBuilder = new DefaultSettingsBuilderFactory().newInstance();
         SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
         settingsRequest
                  .setUserSettingsFile(new File(OSUtils.getUserHomeDir().getAbsolutePath() + "/.m2/settings.xml"));

         if (M2_HOME != null)
            settingsRequest.setGlobalSettingsFile(new File(M2_HOME + "/conf/settings.xml"));

         SettingsBuildingResult settingsBuildingResult = settingsBuilder.build(settingsRequest);
         Settings effectiveSettings = settingsBuildingResult.getEffectiveSettings();

         if (effectiveSettings.getLocalRepository() == null)
         {
            effectiveSettings.setLocalRepository(OSUtils.getUserHomeDir().getAbsolutePath() + "/.m2/repository");
         }

         return effectiveSettings;
      }
      catch (SettingsBuildingException e)
      {
         throw new ProjectModelException(e);
      }
   }

   public ProjectBuilder getBuilder()
   {
      return builder;
   }

   public <T> T lookup(Class<T> type)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      try {
         return getContainer().lookup(type);
      }
      catch (ComponentLookupException e) {
         throw new ProjectModelException("Could not look up component of type [" + type.getName() + "]", e);
      }
      finally
      {
         /*
          * We reset the classloader to prevent potential modules bugs 
          * if Classwords container changes classloaders on us
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
            getContainer().setLoggerManager(loggerManager);

            builder = getContainer().lookup(ProjectBuilder.class);
         }
         catch (Exception e)
         {
            throw new ProjectModelException(
                     "Could not initialize Maven", e);
         }
         finally
         {
            /*
             * We reset the classloader to prevent potential modules bugs 
             * if Classwords container changes classloaders on us
             */
            Thread.currentThread().setContextClassLoader(cl);
         }
      }
      return container;
   }
}
