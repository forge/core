/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.mvn;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.plugins.MavenPluginInstaller;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PathspecParser;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("maven")
@Topic("Project")
@RequiresProject
@RequiresFacet(MavenCoreFacet.class)
@RequiresResource(DirectoryResource.class)
public class MavenPlugin implements Plugin
{
   private final Shell shell;
   private final Project project;
   private final ProjectFactory factory;
   private final ResourceFactory resources;
   private final BeanManager manager;
   private final MavenPluginInstaller mavenPluginInstaller;

   @Inject
   public MavenPlugin(final Shell shell, final Project project, final ProjectFactory factory,
            final ResourceFactory resources, final MavenPluginInstaller mavenPluginInstaller, BeanManager manager)
   {
      this.shell = shell;
      this.project = project;
      this.factory = factory;
      this.resources = resources;
      this.manager = manager;
      this.mavenPluginInstaller = mavenPluginInstaller;
   }

   @Command("set-groupid")
   public void setGroupId(final PipeOut out,
            @Option(description = "the new groupId; for example: \"org.jboss.forge\"") final String groupId)
   {
      Assert.notNull(groupId, "GroupId must not be empty");

      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      pom.setGroupId(groupId);
      mvn.setPOM(pom);
      out.println("Set groupId [ " + groupId + " ]");
   }

   @Command("set-artifactid")
   public void setArtifactId(final PipeOut out,
            @Option(description = "the new artifactId; for example: \"forge-shell\"") final String artifactId)
   {
      Assert.notNull(artifactId, "GroupId must not be empty");

      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      pom.setArtifactId(artifactId);
      mvn.setPOM(pom);
      out.println("Set artifactId [ " + artifactId + " ]");
   }

   @Command("set-version")
   public void setVersion(final PipeOut out,
            @Option(description = "the new version; for example: \"1.0.0.Final\"") final String version)
   {
      Assert.notNull(version, "GroupId must not be empty");

      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      pom.setVersion(version);
      mvn.setPOM(pom);

      out.println("Set version [ " + version + " ]");
   }

   @Command("set-name")
   public void setName(final PipeOut out,
            @Option(description = "the new name; for example: \"UI-Layer\"") final String name)
   {
      Assert.notNull(name, "Name must not be empty");

      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      pom.setName(name);
      mvn.setPOM(pom);

      out.println("Set name [ " + name + " ]");
   }

   @Command("set-parent")
   public void setParent(
            @Option(name = "parentId",
                     description = "dependency identifier of parent, ex: \"org.jboss.forge:forge-parent:1.0.0\"",
                     required = false) final Dependency gav,
            @Option(name = "parentRelativePath",
                     description = "relative location from the current project to the parent project root folder",
                     type = PromptType.FILE_PATH,
                     required = false) final String relativePath,
            @Option(name = "parentProjectRoot",
                     description = "absolute location of a project to use as this project's direct parent",
                     required = false) final Resource<?> path,
            final PipeOut out)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      Parent parent = null;
      if (gav != null)
      {
         Assert.notNull(gav.getArtifactId(), "ArtifactId must not be null [" + gav.toCoordinates() + "]");
         Assert.notNull(gav.getGroupId(), "GroupId must not be null [" + gav.toCoordinates() + "]");
         Assert.notNull(gav.getVersion(), "Version must not be null [" + gav.toCoordinates() + "]");

         parent = new Parent();
         parent.setArtifactId(gav.getArtifactId());
         parent.setGroupId(gav.getGroupId());
         parent.setVersion(gav.getVersion());

         if (relativePath != null)
         {
            parent.setRelativePath(relativePath);
         }

         Model pom = mvn.getPOM();
         pom.setParent(parent);
         mvn.setPOM(pom);
      }
      else if ((path != null) && factory.containsProject(path.reify(DirectoryResource.class)))
      {
         Project parentProject = factory.findProject(path.reify(DirectoryResource.class));
         MavenCoreFacet parentCore = parentProject.getFacet(MavenCoreFacet.class);

         parent = new Parent();
         parent.setArtifactId(parentCore.getMavenProject().getArtifactId());
         parent.setGroupId(parentCore.getMavenProject().getGroupId());
         parent.setVersion(parentCore.getMavenProject().getVersion());

         if (relativePath != null)
         {
            parent.setRelativePath(relativePath);
         }

         Model pom = mvn.getPOM();
         pom.setParent(parent);
         mvn.setPOM(pom);
      }
      else if (relativePath != null)
      {
         PathspecParser parser = new PathspecParser(resources, shell.getCurrentProject().getProjectRoot(), relativePath);
         List<Resource<?>> resolvedResources = parser.resolve();
         if (!resolvedResources.isEmpty()
                  && factory.containsProject(resolvedResources.get(0).reify(DirectoryResource.class)))
         {
            Project parentProject = factory.findProject(resolvedResources.get(0).reify(DirectoryResource.class));
            MavenCoreFacet parentCore = parentProject.getFacet(MavenCoreFacet.class);

            parent = new Parent();
            parent.setArtifactId(parentCore.getMavenProject().getArtifactId());
            parent.setGroupId(parentCore.getMavenProject().getGroupId());
            parent.setVersion(parentCore.getMavenProject().getVersion());
            parent.setRelativePath(relativePath);

            Model pom = mvn.getPOM();
            pom.setParent(parent);
            mvn.setPOM(pom);
         }
         else
         {
            out.print(ShellColor.RED, "***ERROR***");
            out.println(" relative path did not resolve to a Project [" + relativePath + "]");
         }
      }
      else
      {
         out.print(ShellColor.RED, "***ERROR***");
         out.println(" you must specify a path to or dependency id of the parent project.");
      }

      if (parent != null)
      {
         String parentId = parent.getGroupId() + ":" + parent.getArtifactId() + ":"
                  + parent.getVersion() + " ("
                  + (parent.getRelativePath() == null ? " " : parent.getRelativePath() + ")");

         out.println("Set parent [ " + parentId + " ]");
      }
   }

   @Command("remove-parent")
   public void removeParent(final PipeOut out)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      Parent parent = pom.getParent();

      if (parent != null)
      {
         String parentId = parent.getGroupId() + ":" + parent.getArtifactId() + ":"
                  + parent.getVersion() + " ("
                  + (parent.getRelativePath() == null ? " " : parent.getRelativePath() + ")");

         if (shell.promptBoolean("Are you sure you want to remove all parent information from this project? [ "
                  + parentId + "]", false))
         {
            out.println("Removed parent [ " + parentId + " ]");
            pom.setParent(null);
            mvn.setPOM(pom);
         }
         else
         {
            out.println("Aborted...");
         }
      }
      else
      {
         out.println("Nothing to remove...");
      }
   }

   @Command("update")
   public void updateDependencies()
   {
      if (!new VersionUpdater(project, shell, this.factory, manager).update())
      {
         shell.println("No nothing to update");
      }
   }

   @Command("add-plugin")
   public void addPlugin(
            @Option(description = "dependency identifier of plugin, ex: \"org.jboss.forge:forge-maven-plugin:1.0.0.Final\"",
                     required = true) final Dependency gav,
            final PipeOut out)
   {
      MavenPluginBuilder plugin = MavenPluginBuilder.create().setDependency(gav);
      if (!mavenPluginInstaller.isInstalled(project, plugin))
      {
         mavenPluginInstaller.install(project, plugin);
      }
   }

   @Command("add-managed-plugin")
   public void addManagedPlugin(
            @Option(description = "dependency identifier of plugin, ex: \"org.jboss.forge:forge-maven-plugin:1.0.0.Final\"",
                     required = true) final Dependency gav,
            final PipeOut out)
   {
      MavenPluginBuilder plugin = MavenPluginBuilder.create().setDependency(gav);
      if (!mavenPluginInstaller.isInstalled(project, plugin))
      {
         mavenPluginInstaller.installManaged(project, plugin);
      }
   }
}
