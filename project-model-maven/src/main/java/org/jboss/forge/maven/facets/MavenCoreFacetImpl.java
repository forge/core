/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.events.ResourceModified;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.util.NativeSystemCall;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.MavenCoreFacet")
public class MavenCoreFacetImpl extends BaseFacet implements MavenCoreFacet, Facet
{
   private ProjectBuildingResult buildingResult;
   private ProjectBuildingResult fullBuildingResult;

   @Inject
   private MavenContainer container;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private BeanManager manager;

   @Inject
   private ResourceFactory factory;

   public MavenCoreFacetImpl()
   {}

   /*
    * POM manipulation methods
    */
   @Override
   public ProjectBuildingResult getPartialProjectBuildingResult()
   {
      if (this.buildingResult == null)
      {
         ProjectBuildingRequest request = null;
         File pomFile = getPOMFile().getUnderlyingResourceObject();
         try
         {
            // Attempt partial build first
            request = container.getRequest();
            buildingResult = container.getBuilder().build(pomFile, request);
         }
         catch (ProjectBuildingException partial)
         {
            // try full build if that fails
            if (request != null)
            {
               try
               {
                  request.setResolveDependencies(true);
                  buildingResult = container.getBuilder().build(pomFile, request);
                  fullBuildingResult = buildingResult;
               }
               catch (Exception full)
               {
                  throw new ProjectModelException(full);
               }
            }
            else
            {
               throw new ProjectModelException(partial);
            }
         }
      }
      return buildingResult;
   }

   /*
    * POM manipulation methods
    */
   @Override
   public ProjectBuildingResult getFullProjectBuildingResult()
   {
      if (this.fullBuildingResult == null)
      {
         ProjectBuildingRequest request = null;
         request = container.getRequest();
         File pomFile = getPOMFile().getUnderlyingResourceObject();
         if (request != null)
         {
            try
            {
               request.setResolveDependencies(true);
               buildingResult = container.getBuilder().build(pomFile, request);
               fullBuildingResult = buildingResult;
            }
            catch (Exception full)
            {
               throw new ProjectModelException(full);
            }
         }
         else
         {
            throw new ProjectModelException("Project building request was null");
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
   public Model getPOM()
   {
      try
      {
         Model result = new Model();

         // FIXME this should/can-not use the Maven Native file writer if we are going to abstract file APIs
         MavenXpp3Reader reader = new MavenXpp3Reader();
         FileInputStream stream = new FileInputStream(getPOMFile().getUnderlyingResourceObject());
         if (stream.available() > 0)
         {
            result = reader.read(stream);
         }
         stream.close();

         result.setPomFile(getPOMFile().getUnderlyingResourceObject());
         return result;
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not open POM file: " + getPOMFile(), e);
      }
      catch (XmlPullParserException e)
      {
         throw new ProjectModelException("Could not parse POM file: " + getPOMFile(), e);
      }
   }

   @Override
   public void setPOM(final Model pom)
   {
      try
      {
         // FIXME this should/can-not use the Maven Native file writer if we are going to abstract file APIs
         MavenXpp3Writer writer = new MavenXpp3Writer();
         FileWriter fw = new FileWriter(getPOMFile().getUnderlyingResourceObject());
         writer.write(fw, pom);
         fw.close();
         manager.fireEvent(new ResourceModified(getPOMFile()), new Annotation[] {});
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not write POM file: " + getPOMFile(), e);
      }
      invalidateBuildingResults();
   }

   private Model createPOM()
   {
      FileResource<?> pomFile = getPOMFile();
      if (!pomFile.exists())
      {
         pomFile.createNewFile();
      }
      Model pom = getPOM();
      pom.setGroupId("org.jboss.forge.generated");
      pom.setArtifactId("generated-pom");
      pom.setVersion("1.0.0-SNAPSHOT");
      pom.setPomFile(getPOMFile().getUnderlyingResourceObject());
      pom.setModelVersion("4.0.0");
      setPOM(pom);
      return pom;
   }

   @Override
   public FileResource<?> getPOMFile()
   {
      Resource<?> file = project.getProjectRoot().getChild("pom.xml");
      return (FileResource<?>) file;
   }

   @Override
   public boolean isInstalled()
   {
      return getPOMFile().exists();
   }

   @Override
   public boolean install()
   {
      createPOM();
      return true;
   }

   @Override
   public MavenProject getMavenProject()
   {
      return getPartialProjectBuildingResult().getProject();
   }

   @Override
   public boolean executeMavenEmbedded(final String[] parms)
   {
      return executeMavenEmbedded(System.out, System.err, parms);
   }

   @Override
   public boolean executeMavenEmbedded(final PrintStream out, final PrintStream err, String[] parms)
   {
      if ((parms == null) || (parms.length == 0))
      {
         parms = new String[] { "" };
      }
      MavenCli cli = new MavenCli();
      int i = cli.doMain(parms, project.getProjectRoot().getFullyQualifiedName(),
               out, err);
      return i == 0;
   }

   @Override
   public boolean executeMaven(final List<String> parameters)
   {
      return executeMaven(parameters.toArray(new String[] {}));
   }

   @Override
   public boolean executeMaven(final String[] selected)
   {
      return executeMaven(writer, selected);
   }

   @Override
   public boolean executeMaven(final ShellPrintWriter out, final String[] parms)
   {
      try
      {
         return 0 == NativeSystemCall.execFromPath(getMvnCommand(), parms, out, project.getProjectRoot());
      }
      catch (IOException e)
      {
         return executeMavenEmbedded(parms);
      }
   }

   private String getMvnCommand()
   {
      return OSUtils.isWindows() ? "mvn.bat" : "mvn";
   }

   @Override
   public DirectoryResource getLocalRepositoryDirectory()
   {
      return factory.getResourceFrom(new File(container.getSettings().getLocalRepository())).reify(
               DirectoryResource.class);
   }

   @Override
   public String resolveProperties(final String input)
   {
      String result = input;
      if (!Strings.isNullOrEmpty(input))
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
}
