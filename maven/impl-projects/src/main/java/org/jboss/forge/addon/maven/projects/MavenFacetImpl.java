/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.util.NativeSystemCall;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenFacetImpl extends AbstractFacet<Project> implements ProjectFacet, MavenFacet
{
   private static final Logger log = Logger.getLogger(MavenFacetImpl.class.getName());

   @Inject
   private ResourceFactory factory;

   @Inject
   private MavenBuildManager buildManager;

   public ProjectBuildingRequest getRequest()
   {
      return buildManager.getProjectBuildingRequest();
   }

   public ProjectBuildingRequest getOfflineRequest()
   {
      return buildManager.getProjectBuildingRequest(isInstalled());
   }

   public ProjectBuildingRequest getBuildingRequest(final boolean offline)
   {
      return buildManager.getProjectBuildingRequest(offline);
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         MavenModelResource pom = getModelResource();
         if (!pom.createNewFile())
            throw new IllegalStateException("Could not create POM file.");
         pom.setContents(createDefaultPOM());
      }
      return isInstalled();
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
   public MavenModelResource getModelResource()
   {
      return getFaceted().getRootDirectory().getChild("pom.xml").reify(MavenModelResource.class);
   }

   @Override
   public boolean isInstalled()
   {
      MavenModelResource pom = getModelResource();
      return pom != null && pom.exists();
   }

   @Override
   public Model getModel()
   {
      return getModelResource().getCurrentModel();
   }

   @Override
   public void setModel(final Model pom)
   {
      MavenXpp3Writer writer = new MavenXpp3Writer();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try (Writer fw = new OutputStreamWriter(outputStream))
      {
         writer.write(fw, pom);
         getModelResource().setContents(outputStream.toString());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not write POM file: " + getModelResource().getFullyQualifiedName(), e);
      }
   }

   /*
    * POM manipulation methods
    */
   public synchronized ProjectBuildingResult getProjectBuildingResult() throws Exception
   {
      return buildManager.getProjectBuildingResult(getModelResource());
   }

   @Override
   public Map<String, String> getProperties()
   {
      Map<String, String> result = new HashMap<>();

      try
      {
         Properties properties = getProjectBuildingResult().getProject().getProperties();
         for (Entry<Object, Object> o : properties.entrySet())
         {
            result.put((String) o.getKey(), (String) o.getValue());
         }
      }
      catch (Exception e)
      {
         log.log(Level.WARNING, "Failed to resolve properties in [" + getModelResource().getFullyQualifiedName() + "].");
         log.log(Level.FINE, "Failed to resolve properties in Project [" + getModelResource().getFullyQualifiedName()
                  + "].", e);
      }

      return result;
   }

   @Override
   public String resolveProperties(String input)
   {
      String result = input;
      try
      {
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
      }
      catch (Exception e)
      {
         log.log(Level.WARNING, "Failed to resolve properties in [" + getModelResource().getFullyQualifiedName()
                  + "] for input value [" + input + "].");
         log.log(Level.FINE, "Failed to resolve properties in Project [" + getModelResource().getFullyQualifiedName()
                  + "].", e);
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

   @Override
   public boolean executeMavenEmbedded(List<String> parameters, PrintStream out, PrintStream err)
   {
      return executeMavenEmbedded(out, err, parameters.toArray(new String[parameters.size()]));
   }

   public boolean executeMavenEmbedded(final PrintStream out, final PrintStream err, String[] parms)
   {
      if ((parms == null) || (parms.length == 0))
      {
         parms = new String[] { "" };
      }
      MavenCli cli = new MavenCli();
      int i = cli.doMain(parms, getFaceted().getRootDirectory().getFullyQualifiedName(),
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
         int returnValue = NativeSystemCall.execFromPath(getMvnCommand(), parms, out, getFaceted().getRootDirectory());
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
      return factory.create(buildManager.getLocalRepositoryDirectory()).reify(DirectoryResource.class);
   }

   @Override
   public boolean isModelValid()
   {
      try
      {
         getProjectBuildingResult();
         return true;
      }
      catch (Exception e)
      {
         return false;
      }
   }

}
