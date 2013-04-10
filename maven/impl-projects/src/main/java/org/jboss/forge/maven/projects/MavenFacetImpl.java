/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
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
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.jboss.forge.container.util.OperatingSystemUtils;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.environment.Network;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.maven.projects.util.NativeSystemCall;
import org.jboss.forge.maven.projects.util.NullOutputStream;
import org.jboss.forge.maven.projects.util.RepositoryUtils;
import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenFacetImpl extends AbstractFacet<Project> implements ProjectFacet, MavenFacet
{
   private ProjectBuildingRequest request;
   private ProjectBuildingResult buildingResult;
   private ProjectBuildingResult fullBuildingResult;
   private ProjectBuilder builder = null;

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
      if(builder == null)
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
         plexus.lookup(MavenExecutionRequestPopulator.class).populateFromSettings(executionRequest,
                  container.getSettings());
         request = executionRequest.getProjectBuildingRequest();

         ArtifactRepository localRepository = RepositoryUtils.toArtifactRepository("local",
                  new File(settings.getLocalRepository()).toURI().toURL().toString(), null, true, true);
         request.setLocalRepository(localRepository);

         List<ArtifactRepository> settingsRepos = new ArrayList<ArtifactRepository>();
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

         // No repository set, enable central
         if (!offline && settingsRepos.isEmpty())
         {
            settingsRepos.add(RepositoryUtils.toArtifactRepository("CENTRAL", "http://repo1.maven.org/maven2/", null,
                     true, false));
         }

         request.setRemoteRepositories(settingsRepos);
         request.setSystemProperties(System.getProperties());

         MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
         Proxy activeProxy = settings.getActiveProxy();
         if (activeProxy != null)
         {
            DefaultProxySelector dps = new DefaultProxySelector();
            dps.add(RepositoryUtils.convertFromMavenProxy(activeProxy), activeProxy.getNonProxyHosts());
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

   @Override
   public void setOrigin(Project project)
   {
      super.setOrigin(project);
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         MavenPomResource pom = getPomResource();
         if (!pom.createNewFile())
            throw new IllegalStateException("Could not create POM file.");

         pom.setContents(getClass().getClassLoader().getResourceAsStream("/pom-template.xml"));
      }
      return isInstalled();
   }

   @Override
   public MavenPomResource getPomResource()
   {
      return getOrigin().getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);
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
      invalidateBuildingResults();
   }

   public ProjectBuildingResult getPartialProjectBuildingResult()
   {
      if (this.buildingResult == null)
      {
         ProjectBuildingRequest request = null;
         File pomFile = getPomResource().getUnderlyingResourceObject();
         try
         {
            // Attempt partial build first
            request = getRequest();
            buildingResult = getBuilder().build(pomFile, request);
         }
         catch (ProjectBuildingException partial)
         {
            // try full build if that fails
            if (request != null)
            {
               try
               {
                  request.setResolveDependencies(true);
                  buildingResult = getBuilder().build(pomFile, request);
                  fullBuildingResult = buildingResult;
               }
               catch (Exception full)
               {
                  throw new RuntimeException(full);
               }
            }
            else
            {
               throw new RuntimeException(partial);
            }
         }
      }
      return buildingResult;
   }

   /*
    * POM manipulation methods
    */
   public ProjectBuildingResult getFullProjectBuildingResult()
   {
      if (this.fullBuildingResult == null)
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
      this.buildingResult = null;
      this.fullBuildingResult = null;
   }

   @Override
   public String resolveProperties(String input)
   {
      String result = input;
      if (input != null)
      {
         Properties properties = getPartialProjectBuildingResult().getProject().getProperties();

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
      return executeMavenEmbedded(parameters.toArray(new String[] {}));
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
      int i = cli.doMain(parms, getOrigin().getProjectRoot().getFullyQualifiedName(),
               out, err);
      return i == 0;
   }

   @Override
   public boolean executeMaven(final List<String> parameters)
   {
      return executeMaven(parameters.toArray(new String[] {}));
   }

   public boolean executeMaven(final String[] selected)
   {
      return executeMaven(new NullOutputStream(), selected);
   }

   public boolean executeMaven(final OutputStream out, final String[] parms)
   {
      try
      {
         return 0 == NativeSystemCall.execFromPath(getMvnCommand(), parms, out, getOrigin().getProjectRoot());
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
