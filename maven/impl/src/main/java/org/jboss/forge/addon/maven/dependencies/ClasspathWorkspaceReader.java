/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.dependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * {@link WorkspaceReader} implementation capable of reading from the ClassPath
 *
 * Based on the ClasspathWorkspaceReader provided by ShrinkWrap Resolver
 *
 * @author <a href="mailto:ggastakd@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClasspathWorkspaceReader implements WorkspaceReader
{

   private static final Logger log = Logger.getLogger(ClasspathWorkspaceReader.class.getName());

   /**
    * class path entry
    */
   private static final String CLASS_PATH_KEY = "java.class.path";

   /**
    * surefire cannot modify class path for test execution, so it have to store it in a different variable
    */
   private static final String SUREFIRE_CLASS_PATH_KEY = "surefire.test.class.path";

   private final Set<String> classPathEntries = new LinkedHashSet<String>();

   /**
    * Cache classpath File objects and retrieved isFile isDirectory values. Key is a classpath entry
    *
    * @see #getClasspathFile(String)
    */
   private final Map<String, File> classpathFileInfoCache = new HashMap<String, File>();

   /**
    * Cache pom File objects and retrieved isFile isDirectory values. Key - child File
    *
    * @see #getPomFile(java.io.File)
    */
   private final Map<File, File> pomFileInfoCache = new HashMap<File, File>();

   /**
    * Cache Found in classpath artifacts. Key is a pom file.
    *
    * @see #getFoundArtifact(java.io.File)
    */
   private final Map<File, Artifact> foundArtifactCache = new HashMap<File, Artifact>();

   /**
    * Repository unique in this instance
    *
    * @see #getRepository()
    */
   private WorkspaceRepository repository = new WorkspaceRepository("classpath");

   public ClasspathWorkspaceReader()
   {
      final String classPath = System.getProperty(CLASS_PATH_KEY);
      final String surefireClassPath = System.getProperty(SUREFIRE_CLASS_PATH_KEY);
      this.classPathEntries.addAll(getClassPathEntries(surefireClassPath));
      this.classPathEntries.addAll(getClassPathEntries(classPath));
   }

   @Override
   public WorkspaceRepository getRepository()
   {
      return repository;
   }

   @Override
   public File findArtifact(final Artifact artifact)
   {
      for (String classpathEntry : classPathEntries)
      {
         final File file = getClasspathFile(classpathEntry);

         if (file.isDirectory())
         {
            // TODO: This is not reliable, file might have different name
            // FIXME: Surefire might user jar in the classpath instead of the target/classes
            final File pomFile = getPomFile(file);
            if (pomFile.isFile())
            {
               final Artifact foundArtifact = getFoundArtifact(pomFile);

               if (foundArtifact.getGroupId().equals(artifact.getGroupId())
                        && foundArtifact.getArtifactId().equals(artifact.getArtifactId())
                        && foundArtifact.getVersion().equals(artifact.getVersion()))
               {
                  if ("pom".equals(artifact.getExtension()))
                  {
                     return pomFile;
                  }
                  else
                  {
                     return new File(file.getParentFile(), "classes");
                  }
               }
            }
         }
         // this is needed for Surefire when run as 'mvn package'
         else if (file.isFile())
         {
            final StringBuilder name = new StringBuilder(artifact.getArtifactId()).append("-").append(
                     artifact.getVersion());

            // TODO: This is nasty
            // we need to get a a pom.xml file to be sure we fetch transitive deps as well
            if (file.getName().contains(name.toString()))
            {
               if ("pom".equals(artifact.getExtension()))
               {
                  // try to get pom file for the project
                  final File pomFile = new File(file.getParentFile().getParentFile(), "pom.xml");
                  if (pomFile.isFile())
                  {
                     Artifact foundArtifact = getFoundArtifact(pomFile);
                     if (foundArtifact.getGroupId().equals(artifact.getGroupId())
                              && foundArtifact.getArtifactId().equals(artifact.getArtifactId())
                              && foundArtifact.getVersion().equals(artifact.getVersion()))
                     {

                        // System.out
                        // .println("BUILD: ################################# Artifact: " + artifact + " POM: "
                        // + pomFile);
                        return pomFile;
                     }
                  }
               }
               // SHRINKRES-102, consider classifier as well
               String classifier = artifact.getClassifier();
               if (classifier != null && !classifier.isEmpty())
               {
                  name.append("-").append(classifier);
               }

               // we are looking for a non pom artifact, let's get it
               name.append(".").append(artifact.getExtension());
               if (file.getName().equals(name.toString()))
               {
                  // return raw file
                  return file;
               }
            }
         }
      }

      return null;
   }

   @Override
   public List<String> findVersions(final Artifact artifact)
   {
      Set<String> versions = new TreeSet<String>();
      for (String classpathEntry : classPathEntries)
      {
         final File file = getClasspathFile(classpathEntry);

         if (file.isDirectory())
         {
            // TODO: This is not reliable, file might have different name
            // FIXME: Surefire might use jar in the classpath instead of the target/classes
            final File pomFile = getPomFile(file);
            if (pomFile.isFile())
            {
               final Artifact foundArtifact = getFoundArtifact(pomFile);

               if (foundArtifact.getGroupId().equals(artifact.getGroupId())
                        && foundArtifact.getArtifactId().equals(artifact.getArtifactId()))
               {
                  versions.add(foundArtifact.getVersion());
               }
            }
         }
         // this is needed for Surefire when run as 'mvn package'
         else if (file.isFile())
         {
            final StringBuilder name = new StringBuilder(artifact.getArtifactId()).append("-").append(
                     artifact.getVersion());

            // TODO: This is nasty
            // we need to get a a pom.xml file to be sure we fetch transitive deps as well
            if (file.getName().contains(name.toString()))
            {
               if ("pom".equals(artifact.getExtension()))
               {
                  // try to get pom file for the project
                  final File pomFile = new File(file.getParentFile().getParentFile(), "pom.xml");
                  if (pomFile.isFile())
                  {
                     final Artifact foundArtifact = getFoundArtifact(pomFile);

                     if (foundArtifact.getGroupId().equals(artifact.getGroupId())
                              && foundArtifact.getArtifactId().equals(artifact.getArtifactId()))
                     {
                        versions.add(foundArtifact.getVersion());
                     }
                  }
               }
            }
         }
      }
      return new ArrayList<String>(versions);
   }

   private Set<String> getClassPathEntries(final String classPath)
   {
      if (classPath == null || classPath.isEmpty())
      {
         return Collections.emptySet();
      }
      return new LinkedHashSet<String>(Arrays.asList(classPath.split(File.pathSeparator)));
   }

   private File getClasspathFile(final String classpathEntry)
   {
      File classpathFileInfo = classpathFileInfoCache.get(classpathEntry);
      if (classpathFileInfo == null)
      {
         classpathFileInfo = new File(classpathEntry);
         classpathFileInfoCache.put(classpathEntry, classpathFileInfo);
      }
      return classpathFileInfo;
   }

   private File getPomFile(final File childFile)
   {
      File pomFileInfo = pomFileInfoCache.get(childFile);
      if (pomFileInfo == null)
      {
         pomFileInfo = new File(childFile.getParentFile().getParentFile(), "pom.xml");
         pomFileInfoCache.put(childFile, pomFileInfo);
      }
      return pomFileInfo;
   }

   private Artifact getFoundArtifact(final File pomFile)
   {
      Artifact foundArtifact = foundArtifactCache.get(pomFile);
      if (foundArtifact == null)
      {
         foundArtifact = createFoundArtifact(pomFile);
         foundArtifactCache.put(pomFile, foundArtifact);
      }
      return foundArtifact;
   }

   private Artifact createFoundArtifact(final File pomFile)
   {
      try
      {
         if (log.isLoggable(Level.FINE))
         {
            log.fine("Processing " + pomFile.getAbsolutePath() + " for classpath artifact resolution");
         }

         final Node pom = XMLParser.parse(pomFile);

         String groupId = pom.getTextValueForPatternName("groupId");
         String artifactId = pom.getTextValueForPatternName("artifactId");
         String type = pom.getTextValueForPatternName("packaging");
         String version = pom.getTextValueForPatternName("version");

         if (groupId == null || groupId.isEmpty())
         {
            groupId = pom.getTextValueForPatternName("parent/groupId");
         }
         if (type == null || type.isEmpty())
         {
            type = "jar";
         }
         if (version == null || version.isEmpty())
         {
            version = pom.getTextValueForPatternName("parent/version");
         }

         final Artifact foundArtifact = new DefaultArtifact(groupId, artifactId, type, version);
         foundArtifact.setFile(pomFile);
         return foundArtifact;
      }
      catch (final Exception e)
      {
         throw new RuntimeException("Could not parse pom.xml: " + pomFile, e);
      }
   }
}
