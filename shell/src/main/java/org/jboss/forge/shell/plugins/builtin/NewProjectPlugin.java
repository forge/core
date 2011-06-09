/*
 * JBoss, by Red Hat.
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

package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.jboss.forge.project.packaging.PackagingType;
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
   private ResourceFactory factory;

   @SuppressWarnings("unchecked")
   @DefaultCommand
   public void create(
            @Option(name = "named",
                     description = "The name of the new project",
                     required = true) final String name,
            @Option(name = "topLevelPackage",
                     description = "The top-level java package for the project [e.g: \"com.example.project\"] ",
                     required = true,
                     type = PromptType.JAVA_PACKAGE) final String javaPackage,
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

      if (!getValidPackagingTypes().contains(type))
      {
         throw new RuntimeException("Unsupported packaging type: " + type);
      }

      try
      {
         if (projectFolder instanceof FileResource<?>)
         {
            if (!projectFolder.exists())
            {
               ((FileResource<?>) projectFolder).mkdirs();
               dir = projectFolder.reify(DirectoryResource.class);
            }
            else if (projectFolder instanceof DirectoryResource)
            {
               dir = (DirectoryResource) projectFolder;
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

      if (projectFactory.containsProject(dir)
               || !shell.promptBoolean("Use [" + dir.getFullyQualifiedName() + "] as project directory?"))
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

      if (type.equals(PackagingType.JAR) || type.equals(PackagingType.WAR))
      {
         project = projectFactory.createProject(dir,
                  DependencyFacet.class,
                  MetadataFacet.class,
                  JavaSourceFacet.class,
                  ResourceFacet.class);
      }
      else
      {
         project = projectFactory.createProject(dir,
                  DependencyFacet.class,
                  MetadataFacet.class);
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
      return validTypes;
   }
}
