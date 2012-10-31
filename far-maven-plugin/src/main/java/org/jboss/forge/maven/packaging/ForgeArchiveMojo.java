/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.packaging;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Builds a FAR file.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @goal far
 * @phase package
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class ForgeArchiveMojo extends AbstractMojo
{
   private static final String FORGE_XML = "META-INF/forge.xml";
   private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };
   private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

   /**
    * Directory containing the generated JAR.
    *
    * @parameter default-value="${project.build.directory}"
    * @required
    */
   private File outputDirectory;

   /**
    * Directory containing the classes and resource files that should be packaged into the JAR.
    *
    * @parameter default-value="${project.build.outputDirectory}"
    * @required
    */
   private File classesDirectory;

   /**
    * Name of the generated FAR.
    *
    * @parameter alias="farName" property="finalName" default-value="${project.build.finalName}"
    * @required
    */
   private String finalName;

   /**
    * Classifier to add to the generated FAR. If given, the artifact will be an attachment instead. The classifier will
    * not be applied to the JAR file of the project - only to the FAR file.
    *
    * @parameter
    */
   private String classifier;

   /**
    * The Maven project.
    *
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * The JAR archiver needed for archiving the classes directory into a JAR file under WEB-INF/lib.
    *
    * @component role="org.codehaus.plexus.archiver.Archiver" role-hint="jar"
    * @required
    */
   private JarArchiver jarArchiver;

   /**
    * The Maven project's helper.
    *
    * @component
    */
   private MavenProjectHelper projectHelper;

   /**
    * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
    * Archiver Reference</a>.
    *
    * @parameter
    */
   private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

   /**
    * @parameter default-value="${session}"
    * @readonly
    * @required
    */
   private MavenSession session;

   /**
    * Generates an FAR jar and optionally an FAR-client jar.
    */
   public void execute()
            throws MojoExecutionException
   {
      if (getLog().isInfoEnabled())
      {
         getLog().info("Building FAR " + finalName);
      }

      File farFile = getFarFile(outputDirectory, finalName, classifier);

      MavenArchiver archiver = new MavenArchiver();

      archiver.setArchiver(jarArchiver);

      archiver.setOutputFile(farFile);

      File deploymentDescriptor = new File(outputDirectory, FORGE_XML);

      try
      {
         archiver.getArchiver().addDirectory(classesDirectory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);

         if (!deploymentDescriptor.exists())
         {
            // File does not exists, create and add some content
            deploymentDescriptor.getParentFile().mkdir();
            deploymentDescriptor.createNewFile();
            FileUtils.fileWrite(deploymentDescriptor, "<forge/>");
         }
         // Adding descriptor to file
         archiver.getArchiver().addFile(deploymentDescriptor, FORGE_XML);

         // create archive
         archiver.createArchive(session, project, archive);
      }
      catch (ArchiverException e)
      {
         throw new MojoExecutionException("There was a problem creating the FAR archive: " + e.getMessage(), e);
      }
      catch (ManifestException e)
      {
         throw new MojoExecutionException("There was a problem creating the FAR archive: " + e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new MojoExecutionException("There was a problem creating the FAR archive: " + e.getMessage(), e);
      }
      catch (DependencyResolutionRequiredException e)
      {
         throw new MojoExecutionException("There was a problem creating the FAR archive: " + e.getMessage(), e);
      }

      // Handle the classifier if necessary
      if (classifier != null)
      {
         projectHelper.attachArtifact(project, "far", classifier, farFile);
      }
      else
      {
         project.getArtifact().setFile(farFile);
      }
   }

   /**
    * Returns the FAR Jar file to generate, based on an optional classifier.
    *
    * @param basedir the output directory
    * @param finalName the name of the ear file
    * @param classifier an optional classifier
    * @return the FAR file to generate
    */
   private static File getFarFile(File basedir, String finalName, String classifier)
   {
      if (classifier == null)
      {
         classifier = "";
      }
      else if (classifier.trim().length() > 0 && !classifier.startsWith("-"))
      {
         classifier = "-" + classifier;
      }

      return new File(basedir, finalName + classifier + ".far");
   }
}
