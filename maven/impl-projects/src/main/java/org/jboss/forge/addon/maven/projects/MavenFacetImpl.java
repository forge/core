/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.maven.cli.CliRequest;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.cli.logging.impl.UnsupportedSlf4jBindingConfiguration;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblem.Severity;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.util.NativeSystemCall;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.building.BuildMessage;
import org.jboss.forge.addon.projects.building.BuildResult;
import org.jboss.forge.addon.projects.building.BuildResultBuilder;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Strings;

/**
 * Implementation of {@link MavenFacet}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenFacetImpl extends AbstractFacet<Project> implements ProjectFacet, MavenFacet
{
   private static final Logger log = Logger.getLogger(MavenFacetImpl.class.getName());

   private static final MavenBuildManager BUILD_MANAGER = new MavenBuildManager();

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
      return getFaceted().getRoot().getChild("pom.xml").reify(MavenModelResource.class);
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
      MavenModelResource modelResource = getModelResource();
      try
      {
         modelResource.setCurrentModel(pom);
      }
      finally
      {
         BUILD_MANAGER.evictFromCache(modelResource);
      }
   }

   /**
    * POM manipulation methods
    */
   public synchronized ProjectBuildingResult getProjectBuildingResult() throws ProjectBuildingException
   {
      return BUILD_MANAGER.getProjectBuildingResult(getModelResource());
   }

   @Override
   public Model getEffectiveModel()
   {
      try
      {
         return BUILD_MANAGER.getModelBuildingResult(getModelResource()).getEffectiveModel();
      }
      catch (ModelBuildingException e)
      {
         throw new RuntimeException("Error while building effective model", e);
      }
   }

   private synchronized ModelBuildingResult getModelBuildingResult() throws ModelBuildingException
   {
      return BUILD_MANAGER.getModelBuildingResult(getModelResource());
   }

   @Override
   public BuildResult getEffectiveModelBuildResult()
   {
      BuildResultBuilder resultBuilder = BuildResultBuilder.create();
      MavenFacetImpl mvn = getFaceted().getFacet(MavenFacetImpl.class);
      resultBuilder.status(mvn.isModelValid());
      try
      {
         ModelBuildingResult result = mvn.getModelBuildingResult();
         if (!result.getProblems().isEmpty())
         {
            String errorMessage = new ModelBuildingException(result).getMessage();
            resultBuilder.addMessage(BuildMessage.Severity.ERROR, errorMessage);
         }
      }
      catch (ModelBuildingException e)
      {
         resultBuilder.addMessage(BuildMessage.Severity.ERROR, e.getMessage());
      }
      return resultBuilder.build();
   }

   @Override
   public Map<String, String> getProperties()
   {
      Map<String, String> result = new HashMap<>();

      try
      {
         Properties properties = getEffectiveModel().getProperties();
         for (Entry<Object, Object> o : properties.entrySet())
         {
            result.put((String) o.getKey(), (String) o.getValue());
         }
      }
      catch (Exception e)
      {
         log.log(Level.WARNING,
                  "Failed to resolve properties in [" + getModelResource().getFullyQualifiedName() + "].");
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
            Properties properties = getEffectiveModel().getProperties();

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

   public boolean executeMavenEmbedded(final PrintStream out, final PrintStream err, String[] arguments)
   {
      List<String> list = new ArrayList<>();
      // FORGE-1912: Maven settings are not being set in embedded maven
      if (System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION));
      }
      else if (System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION));
      }
      if (arguments != null)
      {
         list.addAll(Arrays.asList(arguments));
      }
      return doExecuteMavenEmbedded(out, err, list);
   }

   /**
    * The embedded maven CLI uses the java logging API to output the log. <br/>
    * Since we never write the log to the console, we need register a logging handler.
    *
    * @author <a href="ggastald@redhat.com">George Gastaldi</a>
    */
   private boolean doExecuteMavenEmbedded(final PrintStream out, final PrintStream err, final List<String> list)
   {
      // Have we asked for quiet mode?
      final boolean quiet = list.contains("-q") || list.contains("--quiet");
      final String[] params = list.toArray(new String[list.size()]);
      // Get root logger. Yes, it is an empty logger, Logger.getGlobal() doesn't work here
      final Logger globalLogger = Logger.getLogger(Strings.EMPTY);
      final Handler outHandler = new UncloseableStreamHandler(out, quiet);
      try
      {
         globalLogger.addHandler(outHandler);
         PrintStream oldout = System.out;
         PrintStream olderr = System.err;
         try
         {
            System.setOut(out);
            System.setErr(err);
            CliRequest cliRequest = createCliRequest(params, getFaceted().getRoot().getFullyQualifiedName());
            int returnCode = new MavenCli().doMain(cliRequest);
            return returnCode == 0;
         }
         finally
         {
            System.setOut(oldout);
            System.setErr(olderr);
         }
      }
      finally
      {
         globalLogger.removeHandler(outHandler);
      }
   }

   // Horrible hack. Bad Maven API
   CliRequest createCliRequest(String[] params, String workingDirectory)
   {
      CliRequest cliRequest = null;
      try
      {
         Constructor<CliRequest> constructor = CliRequest.class.getDeclaredConstructor(String[].class,
                  ClassWorld.class);
         // This is package-private
         constructor.setAccessible(true);
         cliRequest = constructor.newInstance(params, null);

         Field workingDirectoryField = CliRequest.class.getDeclaredField("workingDirectory");
         workingDirectoryField.setAccessible(true);
         workingDirectoryField.set(cliRequest, workingDirectory);

         // This is the reason why we now call MavenCli.doMain(CliRequest)
         Field multiModuleProjectDirectoryField = CliRequest.class.getDeclaredField("multiModuleProjectDirectory");
         multiModuleProjectDirectoryField.setAccessible(true);
         multiModuleProjectDirectoryField.set(cliRequest, new File(workingDirectory));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while creating CliRequest", e);
      }
      return cliRequest;
   }

   @Override public boolean executeMaven(List<String> parameters, PrintStream out, PrintStream err)
   {
      List<String> list = new ArrayList<>();
      // FORGE-1912: Maven settings are not being set in embedded maven
      if (System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION));
      }
      else if (System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION));
      }
      if (parameters != null)
      {
         list.addAll(parameters);
      }
      try
      {
         DirectoryResource directory = getFaceted().getRoot().reify(DirectoryResource.class);
         if (directory == null)
            throw new IllegalStateException("Cannot execute maven build on resources that are not File-based.");
         int returnValue = NativeSystemCall.execFromPath(getMvnCommand(), list.toArray(new String[0]), out, directory);
         switch (returnValue) {
            case 0: return true;
            case -1: return false;
            default:
               return executeMavenEmbedded(list, out, err);
         }
      }
      catch (IOException e)
      {
         return false;
      }
   }

   @Override
   public boolean executeMaven(final List<String> parameters)
   {
      return executeMaven(parameters.toArray(new String[0]));
   }

   public boolean executeMaven(final String[] selected)
   {
      return executeMaven(System.out, selected);
   }

   public boolean executeMaven(final OutputStream out, final String[] arguments)
   {
      List<String> list = new ArrayList<>();
      // FORGE-1912: Maven settings are not being set in embedded maven
      if (System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION));
      }
      else if (System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION) != null)
      {
         list.add("-s");
         list.add(System.getProperty(MavenContainer.ALT_GLOBAL_SETTINGS_XML_LOCATION));
      }
      if (arguments != null)
      {
         list.addAll(Arrays.asList(arguments));
      }
      String[] params = list.toArray(new String[list.size()]);
      try
      {
         DirectoryResource directory = getFaceted().getRoot().reify(DirectoryResource.class);
         if (directory == null)
            throw new IllegalStateException("Cannot execute maven build on resources that are not File-based.");
         int returnValue = NativeSystemCall.execFromPath(getMvnCommand(), params, out, directory);
         if (returnValue == 0)
            return true;
         else
            return executeMavenEmbedded(params);
      }
      catch (InterruptedIOException e) {
         return false;
      }
      catch (IOException e)
      {
         return executeMavenEmbedded(params);
      }
   }

   private String getMvnCommand()
   {
      return OperatingSystemUtils.isWindows() ? "mvn.cmd" : "mvn";
   }

   @Override
   public DirectoryResource getLocalRepositoryDirectory()
   {
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
      return resourceFactory.create(BUILD_MANAGER.getLocalRepositoryDirectory()).reify(DirectoryResource.class);
   }

   @Override
   public boolean isModelValid()
   {
      boolean valid = true;
      try
      {
         List<ModelProblem> problems = getModelBuildingResult().getProblems();
         for (ModelProblem problem : problems)
         {
            // It is valid only if all messages are just warnings
            valid &= Severity.WARNING.equals(problem.getSeverity());
         }
      }
      catch (ModelBuildingException mbe)
      {
         valid = false;
      }
      return valid;
   }

   /**
    * A {@link Handler} implementation that writes to a {@link PrintStream}
    *
    * Used in {@link MavenFacetImpl#executeMavenEmbedded(PrintStream, PrintStream, String[])}
    *
    * @author <a href="ggastald@redhat.com">George Gastaldi</a>
    */
   private static class UncloseableStreamHandler extends Handler
   {
      private final PrintStream out;
      private final boolean quiet;

      public UncloseableStreamHandler(PrintStream out, boolean quiet)
      {
         super();
         this.out = out;
         this.quiet = quiet;
      }

      @Override
      public void publish(LogRecord record)
      {
         // Write only if quiet is false
         if (!quiet && isLoggable(record))
         {
            out.printf("[%s] %s%n", record.getLevel(), record.getMessage());
         }
      }

      @Override
      public boolean isLoggable(LogRecord record)
      {
         if (super.isLoggable(record))
         {
            // Skip annoying SLF4J logging messages
            if (UnsupportedSlf4jBindingConfiguration.class.getName().equals(record.getLoggerName()))
            {
               return false;
            }
            return true;
         }
         return false;
      }

      @Override
      public void flush()
      {
         out.flush();
      }

      @Override
      public void close()
      {
         // Never close
      }
   }
}
