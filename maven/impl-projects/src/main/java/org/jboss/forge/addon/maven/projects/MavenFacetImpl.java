/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.ProjectBuilder;
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
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.environment.Network;
import org.jboss.forge.addon.maven.projects.util.NativeSystemCall;
import org.jboss.forge.addon.maven.projects.util.RepositoryUtils;
import org.jboss.forge.addon.maven.resources.MavenPomResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.monitor.ResourceListener;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenFacetImpl extends AbstractFacet<Project> implements ProjectFacet, MavenFacet
{
   private ProjectBuildingRequest request;
   private ProjectBuildingResult buildingResult;
   private ProjectBuildingResult fullBuildingResult;
   private ProjectBuilder builder = null;
   private ResourceMonitor monitor;
   private volatile boolean invalidateBuildingResult;

   @Inject
   private MavenContainer container;

   @Inject
   private Environment environment;

   @Inject
   private ResourceFactory factory;

   @Inject
   private PlexusContainer plexus;

   public ProjectBuilder getBuilder()
   {
      if (builder == null)
         builder = plexus.lookup(ProjectBuilder.class);
      return builder;
   }

   public ProjectBuildingRequest getRequest()
   {
      return getBuildingRequest(Network.isOffline(environment));
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
         Settings settings = container.getSettings();
         // TODO this needs to be configurable via .forge
         // TODO this reference to the M2_REPO should probably be centralized

         MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
         MavenExecutionRequestPopulator populator = plexus.lookup(MavenExecutionRequestPopulator.class);
         populator.populateFromSettings(executionRequest, container.getSettings());
         populator.populateDefaults(executionRequest);
         RepositorySystem system = plexus.lookup(RepositorySystem.class);
         request = executionRequest.getProjectBuildingRequest();

         ArtifactRepository localRepository = RepositoryUtils.toArtifactRepository("local",
                  new File(settings.getLocalRepository()).toURI().toURL().toString(), null, true, true);
         request.setLocalRepository(localRepository);

         List<ArtifactRepository> settingsRepos = new ArrayList<ArtifactRepository>(request.getRemoteRepositories());
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

   @Override
   public void setFaceted(Project project)
   {
      super.setFaceted(project);
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         MavenPomResource pom = getPomResource();
         if (!pom.createNewFile())
            throw new IllegalStateException("Could not create POM file.");
         pom.setContents(createDefaultPOM());
         monitor = pom.monitor();
         monitor.addResourceListener(new ResourceListener()
         {
            @Override
            public void processEvent(ResourceEvent event)
            {
               invalidateBuildingResults();
            }
         });
      }
      return isInstalled();
   }

   @Override
   public boolean uninstall()
   {
      if (monitor != null)
      {
         monitor.cancel();
      }
      return super.uninstall();
   }

   private String createDefaultPOM()
   {
      MavenXpp3Writer writer = new MavenXpp3Writer();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      org.apache.maven.project.MavenProject mavenProject = new org.apache.maven.project.MavenProject();
      mavenProject.setModelVersion("4.0.0");
      try
      {
         writer.write(baos, mavenProject.getModel());
         return baos.toString();
      }
      catch (IOException e)
      {
         // Should not happen
         throw new RuntimeException("Failed to create default pom.xml", e);
      }
   }

   @Override
   public MavenPomResource getPomResource()
   {
      return getFaceted().getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);
   }

   @Override
   public boolean isInstalled()
   {
      MavenPomResource pom = getPomResource();
      return pom != null && pom.exists();
   }

   @Override
   public Model getPOM()
   {
      return getPomResource().getCurrentModel();
   }

   @Override
   public void setPOM(final Model pom)
   {
      MavenXpp3Writer writer = new MavenXpp3Writer();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Writer fw = new OutputStreamWriter(outputStream);
      try
      {
         writer.write(fw, pom);
         getPomResource().setContents(outputStream.toString());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not write POM file: " + getPomResource().getFullyQualifiedName(), e);
      }
      finally
      {
         try
         {
            fw.close();
            outputStream.close();
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

      /*
       * Invalidate build result immediately; otherwise, the current thread may not get correct results until the
       * monitor thread catches the change.
       */
      invalidateBuildingResults();
   }

   /*
    * POM manipulation methods
    */
   public synchronized ProjectBuildingResult getProjectBuildingResult()
   {
      if (this.buildingResult == null || this.fullBuildingResult == null || invalidateBuildingResult)
      {
         ProjectBuildingRequest request = null;
         request = getRequest();
         File pomFile = getPomResource().getUnderlyingResourceObject();
         if (request != null)
         {
            try
            {
               request.setResolveDependencies(true);
               buildingResult = getBuilder().build(pomFile, request);
               fullBuildingResult = buildingResult;
               invalidateBuildingResult = false;
            }
            catch (RuntimeException full)
            {
               throw full;
            }
            catch (Exception full)
            {
               throw new RuntimeException(full);
            }
         }
         else
         {
            throw new RuntimeException("Project building request was null");
         }
      }
      return fullBuildingResult;
   }

   private void invalidateBuildingResults()
   {
      this.invalidateBuildingResult = true;
   }

   @Override
   public Map<String, String> getProperties()
   {
      Properties properties = getProjectBuildingResult().getProject().getProperties();
      Map<String, String> result = new HashMap<String, String>();
      for (Entry<Object, Object> o : properties.entrySet())
      {
         result.put((String) o.getKey(), (String) o.getValue());
      }
      return result;
   }

   @Override
   public String resolveProperties(String input)
   {
      String result = input;
      if (input != null)
      {
         Properties properties = getProjectBuildingResult().getProject().getProperties();

         for (Entry<Object, Object> e : properties.entrySet())
         {
            String key = "\\$\\{" + e.getKey().toString() + "\\}";
            Object value = e.getValue();
            result = result.replaceAll(key, value.toString());
         }
      }

      return result;
   }

   @Override
   public boolean executeMavenEmbedded(final List<String> parameters)
   {
      return executeMavenEmbedded(parameters.toArray(new String[parameters.size()]));
   }

   public boolean executeMavenEmbedded(final String[] parms)
   {
      return executeMavenEmbedded(System.out, System.err, parms);
   }

   public boolean executeMavenEmbedded(final PrintStream out, final PrintStream err, String[] parms)
   {
      if ((parms == null) || (parms.length == 0))
      {
         parms = new String[] { "" };
      }
      MavenCli cli = new MavenCli();
      int i = cli.doMain(parms, getFaceted().getProjectRoot().getFullyQualifiedName(),
               out, err);
      return i == 0;
   }

   @Override
   public boolean executeMaven(final List<String> parameters)
   {
      return executeMaven(parameters.toArray(new String[parameters.size()]));
   }

   public boolean executeMaven(final String[] selected)
   {
      // return executeMaven(new NullOutputStream(), selected);
      return executeMaven(System.out, selected);
   }

   public boolean executeMaven(final OutputStream out, final String[] parms)
   {
      try
      {
         int returnValue = NativeSystemCall.execFromPath(getMvnCommand(), parms, out, getFaceted().getProjectRoot());
         if (returnValue == 0)
            return true;
         else
            return executeMavenEmbedded(parms);
      }
      catch (IOException e)
      {
         return executeMavenEmbedded(parms);
      }
   }

   private String getMvnCommand()
   {
      return OperatingSystemUtils.isWindows() ? "mvn.bat" : "mvn";
   }

   @Override
   public DirectoryResource getLocalRepositoryDirectory()
   {
      return factory.create(new File(container.getSettings().getLocalRepository())).reify(
               DirectoryResource.class);
   }

}
