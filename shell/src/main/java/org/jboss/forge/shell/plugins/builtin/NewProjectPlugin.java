/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectAssociationProvider;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceException;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.shell.util.ResourceUtil;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("new-project")
@Topic("Project")
@Help("Create a new project in an empty directory.")
public class NewProjectPlugin implements Plugin
{
   @Inject
   private Shell shell;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   Instance<ProjectAssociationProvider> providers;

   @Inject
   private ResourceFactory factory;

   @SuppressWarnings("unchecked")
   @DefaultCommand
   public void create(
            @Option(name = "named",
                     description = "The name of the new project",
                     required = true) final String name,
            @Option(name = "topLevelPackage",
                     description = "The top-level java package for the project [e.g: \"com.example.project\"] ",
                     required = false,
                     type = PromptType.JAVA_PACKAGE) final String suggestedJavaPackage,
            @Option(name = "type",
                     description = "The project type, defaults to .jar",
                     required = false,
                     completer = NewProjectPackagingTypeCompleter.class,
                     defaultValue = "JAR") final PackagingType type,
            @Option(name = "projectFolder",
                     description = "The folder in which to create this project [e.g: \"~/Desktop/...\"] ",
                     required = false) final Resource<?> projectFolder,
            @Option(name = "createMain",
                     description = "Toggle creation of a simple Main() script in the root package, valid for jar projects only",
                     required = false,
                     defaultValue = "false",
                     flagOnly = true) final boolean createMain,
            @Option(name = "finalName",
                     description = "The final artifact name of the new project") final String finalName,
            final PipeOut out
            ) throws IOException
   {
      DirectoryResource dir = null;
      String javaPackage = suggestedJavaPackage;
      if (!getValidPackagingTypes().contains(type))
      {
         throw new RuntimeException("Unsupported packaging type: " + type);
      }

      // FORGE-571
      if (javaPackage == null)
      {
         javaPackage = "com.example." + name;
      }
      boolean skipFolderPrompt = false;
      try
      {
         if (projectFolder instanceof FileResource<?>)
         {
            if (!projectFolder.exists())
            {
               ((FileResource<?>) projectFolder).mkdirs();
               dir = projectFolder.reify(DirectoryResource.class);
               skipFolderPrompt = true;
            }
            else if (projectFolder instanceof DirectoryResource)
            {
               dir = (DirectoryResource) projectFolder;
               skipFolderPrompt = true;
            }
            else
            {
               ShellMessages.error(out, "File exists but is not a directory [" + projectFolder.getFullyQualifiedName()
                        + "]");
            }
         }

         if (dir == null)
         {
            dir = shell.getCurrentDirectory().getChildDirectory(name);
         }
      }
      catch (ResourceException e)
      {
      }

      if (!skipFolderPrompt && (projectFactory.containsProject(dir)
               || !shell.promptBoolean("Use [" + dir.getFullyQualifiedName() + "] as project directory?")))
      {
         if (projectFactory.containsProject(dir))
         {
            ShellMessages.error(out, "[" + dir.getFullyQualifiedName()
                     + "] already contains a project; please use a different folder.");
         }

         if (shell.getCurrentResource() == null)
         {
            dir = ResourceUtil.getContextDirectory(factory.getResourceFrom(Files.getWorkingDirectory()));
         }
         else
         {
            dir = shell.getCurrentDirectory();
         }

         FileResource<?> newDir;
         do
         {
            newDir = shell.getCurrentDirectory();
            shell.println();
            if (!projectFactory.containsProject(newDir.reify(DirectoryResource.class)))
            {
               newDir = shell.promptFile(
                        "Where would you like to create the project? [Press ENTER to use the current directory: "
                                 + newDir + "]", dir);
            }
            else
            {
               newDir = shell.promptFile("Where would you like to create the project?");
            }

            if (!newDir.exists())
            {
               newDir.mkdirs();
               newDir = newDir.reify(DirectoryResource.class);
            }
            else if (newDir.isDirectory() && !projectFactory.containsProject(newDir.reify(DirectoryResource.class)))
            {
               newDir = newDir.reify(DirectoryResource.class);
            }
            else
            {
               ShellMessages.error(out, "That folder already contains a project [" + newDir.getFullyQualifiedName()
                        + "], please select a different location.");
               newDir = null;
            }

         }
         while ((newDir == null) || !(newDir instanceof DirectoryResource));

         dir = (DirectoryResource) newDir;
      }

      if (!dir.exists())
      {
         dir.mkdirs();
      }

      Project project = null;

      if (type.equals(PackagingType.JAR) || type.equals(PackagingType.BUNDLE))
      {
         project = projectFactory.createProject(dir,
                  DependencyFacet.class,
                  MetadataFacet.class,
                  JavaSourceFacet.class,
                  ResourceFacet.class);
      }
      else if (type.equals(PackagingType.WAR))
      {
         project = projectFactory.createProject(dir,
                  DependencyFacet.class,
                  MetadataFacet.class,
                  WebResourceFacet.class,
                  JavaSourceFacet.class,
                  ResourceFacet.class);
      }
      else
      {
         project = projectFactory.createProject(dir,
                  DependencyFacet.class,
                  MetadataFacet.class);
      }

      DirectoryResource parentDir = project.getProjectRoot().getParent().reify(DirectoryResource.class);
      if (parentDir != null)
      {
         for (ProjectAssociationProvider provider : providers)
         {
            if (provider.canAssociate(project, parentDir)
                     && shell.promptBoolean("Add new project as a sub-project of [" + parentDir.getFullyQualifiedName()
                              + "]?"))
            {
               provider.associate(project, parentDir);
            }
         }
      }

      MetadataFacet meta = project.getFacet(MetadataFacet.class);
      meta.setProjectName(name);
      meta.setTopLevelPackage(javaPackage);

      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      packaging.setPackagingType(type);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      deps.addRepository(KnownRepository.JBOSS_NEXUS);

      if (packaging.getPackagingType().equals(PackagingType.JAR) && createMain)
      {
         project.getFacet(JavaSourceFacet.class).saveJavaSource(JavaParser
                  .create(JavaClass.class)
                  .setPackage(javaPackage)
                  .setName("Main")
                  .addMethod("public static void main(String[] args) {}")
                  .setBody("System.out.println(\"Hi there! I was forged as part of the project you call " + name
                           + ".\");")
                  .getOrigin());
      }

      if (project.hasFacet(JavaSourceFacet.class))
      {
         DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();
         createTopLevelPackage(sourceFolder, javaPackage);
      }

      if (finalName != null)
      {
         packaging.setFinalName(finalName);
      }
      else
      {
         packaging.setFinalName(name);
      }

      if (project.hasFacet(ResourceFacet.class))
      {
         project.getFacet(ResourceFacet.class).createResource("<forge/>".toCharArray(), "META-INF/forge.xml");
      }
      /*
       * Only change the environment after success!
       */
      shell.setCurrentResource(project.getProjectRoot());
      ShellMessages.success(out,
               "Created project [" + name + "] in new working directory [" + dir.getFullyQualifiedName() + "]");
   }

   private List<PackagingType> getValidPackagingTypes()
   {
      List<PackagingType> validTypes = new ArrayList<PackagingType>();
      validTypes.add(PackagingType.BASIC);
      validTypes.add(PackagingType.JAR);
      validTypes.add(PackagingType.WAR);
      validTypes.add(PackagingType.BUNDLE);
      return validTypes;
   }

   private DirectoryResource createTopLevelPackage(DirectoryResource sourceFolder, String javaPackage)
   {
      DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(javaPackage));
      directory.mkdirs();
      return directory;
   }

}
