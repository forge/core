/*
 * JBoss, by Red Hat.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.shell.plugins.builtin.project;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.facets.events.RemoveFacets;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("project")
@Topic("Project")
@RequiresProject
@RequiresFacet({ DependencyFacet.class, MavenPluginFacet.class, PackagingFacet.class })
@Help("Perform actions involving the project status, build system, or dependency management system.")
public class ProjectPlugin implements Plugin
{

   private Project project;
   private Shell shell;
   private FacetFactory factory;
   private Event<InstallFacets> installFacets;
   private Event<RemoveFacets> removeFacets;
   private DependencyInstaller dependencyInstaller;

   public ProjectPlugin()
   {
   }

   @Inject
   public ProjectPlugin(final Project project, final Shell shell, final FacetFactory factory,
            final Event<InstallFacets> installFacets, final Event<RemoveFacets> removeFacets,
            final DependencyInstaller installer)
   {
      this.project = project;
      this.shell = shell;
      this.factory = factory;
      this.installFacets = installFacets;
      this.removeFacets = removeFacets;
      this.dependencyInstaller = installer;
   }

   @DefaultCommand
   public void info(final PipeOut out)
   {
      out.print(ShellColor.BOLD, " name: ");
      out.println(project.getFacet(MetadataFacet.class).getProjectName());
      out.print(ShellColor.BOLD, " groupId:  ");
      out.println(project.getFacet(MetadataFacet.class).getTopLevelPackage());
      out.print(ShellColor.BOLD, " final artifact:  ");
      out.println(project.getFacet(PackagingFacet.class).getFinalArtifact().getName());
      out.print(ShellColor.BOLD, " packaging:  ");
      out.println(project.getFacet(PackagingFacet.class).getPackagingType().getType());
      out.print(ShellColor.BOLD, " dir:  ");
      out.println(project.getProjectRoot().getFullyQualifiedName());
   }

   @Command("install-facet")
   public void installFacet(@Option(required = true,
            completer = AvailableFacetsCompleter.class,
            description = "Name of the facet to install") final String facetName)
   {
      try
      {
         Facet facet = factory.getFacetByName(facetName);
         installFacets.fire(new InstallFacets(facet.getClass()));
      }
      catch (FacetNotFoundException e)
      {
         throw new RuntimeException("Could not find a facet with the name: " + facetName
                  + "; use 'project list-facets' to list all available facets.", e);
      }
   }

   @Command("remove-facet")
   public void removeFacet(@Option(required = true,
            completer = InstalledFacetsCompleter.class,
            description = "Name of the facet to install") final String facetName)
   {
      try
      {
         Facet facet = factory.getFacetByName(facetName);
         removeFacets.fire(new RemoveFacets(facet.getClass()));
      }
      catch (FacetNotFoundException e)
      {
         throw new RuntimeException("Could not find a facet with the name: " + facetName
                  + "; use 'project list-facets' to list all available facets.", e);
      }
   }

   /*
    * Dependency manipulation
    */
   @Command(value = "add-dependency", help = "Add a dependency to this project.")
   public void addDep(
            @Option(required = true,
                     type = PromptType.DEPENDENCY_ID,
                     description = "[ groupId:artifactId {:version :scope :packaging} ]",
                     help = "dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"") Dependency gav,
            @Option(type = PromptType.DEPENDENCY_ID,
                     name = "exclude",
                     description = "[ groupId:artifactId ]",
                     help = "exclusion identifier, ex: \"org.jboss.forge:forge-api\"") final Dependency exclusion,
            final PipeOut out
            )
   {
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);
      final boolean hasEffectiveManagedDependency = deps.hasEffectiveManagedDependency(gav);
      Dependency gavCopy = DependencyBuilder.create(gav);

      if (hasEffectiveManagedDependency)
      {
         Dependency existingDep = deps.getEffectiveManagedDependency(gav);
         if (!shell.promptBoolean(String.format("Dependency is managed [%s:%s:%s], reference the managed dependency?",
                  existingDep.getGroupId(), existingDep.getArtifactId(), existingDep.getVersion()), true))
         {
            if (Strings.isNullOrEmpty(gavCopy.getVersion()))
            {
               gavCopy = shell.promptChoiceTyped("Add which version?", deps.resolveAvailableVersions(gavCopy));
            }
         }
      }
      this.dependencyInstaller.install(project, gavCopy);
   }

   @Command(value = "find-dependency", help = "Search for dependencies in all configured project repositories.")
   public void searchDep(
            @Option(required = true,
                     help = "dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"",
                     description = "[ groupId:artifactId {:version:scope:packaging} ]",
                     type = PromptType.DEPENDENCY_ID
            ) Dependency gav,
            final PipeOut out
            )
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      if ((gav.getVersion() == null) || gav.getVersion().trim().isEmpty())
      {
         gav = DependencyBuilder.create(gav).setVersion("[0,)");
      }
      List<Dependency> versions = deps.resolveAvailableVersions(gav);

      for (Dependency dep : versions)
      {
         out.println(DependencyBuilder.toString(dep));
      }

      if (versions.isEmpty())
      {
         out.println("No artifacts found for the query [" + gav + "]");
      }
   }

   @Command(value = "remove-dependency", help = "Remove a dependency from this project")
   public void removeDep(
            @Option(required = true,
                     type = PromptType.DEPENDENCY_ID,
                     description = "[ groupId :artifactId {:version :scope :packaging} ]",
                     help = "dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"") final Dependency gav,
            final PipeOut out
            )
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      if (deps.hasEffectiveDependency(gav))
      {
         deps.removeDependency(gav);
         out.println("Removed dependency [" + gav + "]");
      }
      else
      {
         out.println("Dependency [" + gav + "] not found in project... ");
      }
   }

   @Command(value = "list-dependencies", help = "List all dependencies this project includes")
   public void listDeps(final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      for (Dependency dep : deps.getDependencies())
      {
         printDep(out, dep);
      }
   }

   /*
    * Managed Dependency manipulation
    */
   @Command(value = "add-managed-dependency", help = "Add a managed dependency to this project.")
   public void addManDep(
            @Option(required = true,
                     type = PromptType.DEPENDENCY_ID,
                     description = "[ groupId :artifactId {:version :scope :packaging} ]",
                     help = "managed dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"") Dependency gav,
            final PipeOut out
            )
   {
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);

      if (!manDeps.hasEffectiveManagedDependency(gav)
               || shell.promptBoolean(
                        "Managed dependency already exists [" + gav.getGroupId() + ":" + gav.getArtifactId()
                                 + "], continue?", true))
      {
         DependencyBuilder search = DependencyBuilder.create(gav).setVersion("[0,)");
         List<Dependency> availableVersions = manDeps.resolveAvailableVersions(search);

         if (availableVersions.isEmpty())
         {
            throw new RuntimeException("No available versions resolved for managed dependency [" + gav + "]");
         }

         if (!availableVersions.contains(gav))
         {
            ShellMessages.info(out, "No artifact found for managed dependency [" + gav + "]");
            if (availableVersions.size() > 1)
            {
               gav = shell.promptChoiceTyped("Add which version?", availableVersions);
            }
            else if (shell.promptBoolean("Use [" + availableVersions.get(0) + "] instead?", true))
            {
               gav = availableVersions.get(0);
            }
            else
            {
               throw new RuntimeException("Could not add managed dependency [" + gav + "]");
            }
         }

         if (manDeps.hasEffectiveManagedDependency(gav))
         {
            Dependency managedDependency = manDeps.getManagedDependency(gav);
            manDeps.removeManagedDependency(managedDependency);
         }
         manDeps.addManagedDependency(gav);
         out.println("Added managed dependency [" + gav + "]");
      }
      else
      {
         ShellMessages.info(out, "Aborted.");
      }
   }

   @Command(value = "find-managed-dependency", help = "Search for managed dependencies in all configured project repositories.")
   public void searchManDep(
            @Option(required = true,
                     help = "managed dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"",
                     description = "[ groupId:artifactId {:version:scope:packaging} ]",
                     type = PromptType.DEPENDENCY_ID
            ) Dependency gav,
            @Option(required = false,
                     flagOnly = true,
                     help = "Perform a search only within the locally configured repository",
                     name = "offlineSearch"
            ) final boolean offline,
            final PipeOut out
            )
   {
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);
      if ((gav.getVersion() == null) || gav.getVersion().trim().isEmpty())
      {
         gav = DependencyBuilder.create(gav).setVersion("[0,)");
      }
      List<Dependency> versions = manDeps.resolveAvailableVersions(gav);

      for (Dependency manDep : versions)
      {
         out.println(DependencyBuilder.toString(manDep));
      }

      if (versions.isEmpty())
      {
         out.println("No artifacts found for the query [" + gav + "]");
      }
   }

   @Command(value = "remove-managed-dependency", help = "Remove a managed dependency from this project")
   public void removeManDep(
            @Option(required = true,
                     type = PromptType.DEPENDENCY_ID,
                     description = "[ groupId :artifactId {:version :scope :packaging} ]",
                     help = "managed dependency identifier, ex: \"org.jboss.forge:forge-api:1.0.0\"") final Dependency gav,
            final PipeOut out
            )
   {
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);
      if (manDeps.hasEffectiveManagedDependency(gav))
      {
         manDeps.removeManagedDependency(gav);
         out.println("Removed managed dependency [" + gav + "]");
      }
      else
      {
         out.println("Managed dependency [" + gav + "] not found in project... ");
      }
   }

   @Command(value = "list-managed-dependencies", help = "List all managed dependencies this project includes")
   public void listManDeps(final PipeOut out)
   {
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);

      for (Dependency manDep : manDeps.getManagedDependencies())
      {
         printDep(out, manDep);
      }
   }

   /*
    * Property manipulation
    */
   @Command("set-property")
   public void addProp(
            @Option(required = true,
                     name = "name",
                     completer = DependencyPropertyCompleter.class) final String name,
            @Option(required = true,
                     name = "value") final String value,
            final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (deps.getProperties().containsKey(name) &&
               shell.promptBoolean("Update property [" + name + "=" + deps.getProperty(name) + "] to new value ["
                        + value + "]", true))
      {
         deps.setProperty(name, value);
         out.println("Updated...");
      }
      else
      {
         deps.setProperty(name, value);
         out.println("Set property [" + name + "=" + value + "]");
      }
   }

   @Command("remove-property")
   public void removeProp(
            @Option(required = true, description = "propname",
                     completer = DependencyPropertyCompleter.class) final String name,
            final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      if (deps.getProperties().containsKey(name))
      {
         String value = deps.removeProperty(name);
         out.println("Removed property [" + name + "=" + value + "]");
      }
      else
      {
         out.println("No such property [" + name + "]");
      }
   }

   @Command("list-properties")
   public void listProps(final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      Map<String, String> properties = deps.getProperties();

      for (Entry<String, String> entry : properties.entrySet())
      {
         out.print(entry.getKey() + "=");
         out.println(ShellColor.BLUE, entry.getValue());
      }
   }

   @Command("list-facets")
   public void listFacets(PipeOut out)
   {
      Set<Class<? extends Facet>> facets = factory.getFacetTypes();

      out.println(ShellColor.BOLD, "NOT INSTALLED");
      for (Class<? extends Facet> type : facets)
      {
         String name = ConstraintInspector.getName(type);
         if (!project.hasFacet(type))
         {
            out.println("- " + name + "\t[" + type.getName() + "]");
         }
      }

      out.println();

      out.println(ShellColor.BOLD, "INSTALLED");
      for (Class<? extends Facet> type : facets)
      {
         String name = ConstraintInspector.getName(type);
         if (project.hasFacet(type) && !project.getFacet(type).isInstalled())
         {
            out.println(ShellColor.RED, "? " + name + "\t[" + type.getName()
                     + " - WARN: facet is no longer available]");
         }
         else if (project.hasFacet(type))
         {
            out.println(ShellColor.GREEN, "+ " + name + "\t[" + type.getName() + "]");
         }
      }
   }

   /*
    * Repositories
    */
   @Command("add-known-repository")
   public void repoAdd(
            @Option(description = "type...", required = true) final KnownRepository repo,
            final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (deps.hasRepository(repo))
      {
         out.println("Repository exists [" + repo.name() + "->" + repo.getUrl() + "]");
      }
      else
      {
         deps.addRepository(repo);
         out.println("Added repository [" + repo.name() + "->" + repo.getUrl() + "]");
      }
   }

   @Command("add-repository")
   public void repoAdd(
            @Option(description = "repository name...", required = true) final String name,
            @Option(description = "repository URL...", required = true) final String url,
            final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (deps.hasRepository(url))
      {
         out.println("Repository exists [" + url + "]");
      }
      else
      {
         deps.addRepository(name, url);
         out.println("Added repository [" + name + "->" + url + "]");
      }
   }

   @Command("remove-repository")
   public void repoRemove(
            @Option(required = true, description = "repo url...",
                     completer = RepositoryCompleter.class) final String url,
            final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      DependencyRepository rep;
      if ((rep = deps.removeRepository(url)) != null)
      {
         out.println("Removed repository [" + rep.getId() + "->" + rep.getUrl() + "]");
      }
      else
      {
         out.println("No repository with url [" + url + "]");
      }
   }

   @Command("list-repositories")
   public void repoList(final PipeOut out)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      List<DependencyRepository> repos = deps.getRepositories();

      for (DependencyRepository repo : repos)
      {
         out.print(repo.getId() + "->");
         out.println(ShellColor.BLUE, repo.getUrl());
      }
   }

   @Command("list-plugin-repositories")
   public void pluginRepoList(final PipeOut out)
   {
      MavenPluginFacet deps = project.getFacet(MavenPluginFacet.class);
      List<DependencyRepository> repos = deps.getPluginRepositories();

      for (DependencyRepository repo : repos)
      {
         out.print(repo.getId() + "->");
         out.println(ShellColor.BLUE, repo.getUrl());
      }
   }

   @Command("add-known-plugin-repository")
   public void pluginRepoAdd(
            @Option(description = "type...", required = true) final MavenPluginFacet.KnownRepository repo,
            final PipeOut out)
   {
      MavenPluginFacet deps = project.getFacet(MavenPluginFacet.class);

      if (deps.hasPluginRepository(repo))
      {
         out.println("Plugin repository exists [" + repo.name() + "->" + repo.getUrl() + "]");
      }
      else
      {
         deps.addPluginRepository(repo);
         out.println("Added plugin repository [" + repo.name() + "->" + repo.getUrl() + "]");
      }
   }

   @Command("add-plugin-repository")
   public void pluginRepoAdd(
            @Option(description = "repository name...", required = true) final String name,
            @Option(description = "repository URL...", required = true) final String url,
            final PipeOut out)
   {
      MavenPluginFacet deps = project.getFacet(MavenPluginFacet.class);

      if (deps.hasPluginRepository(url))
      {
         out.println("Plugin repository exists [" + url + "]");
      }
      else
      {
         deps.addPluginRepository(name, url);
         out.println("Added plugin repository [" + name + "->" + url + "]");
      }
   }

   @Command("remove-plugin-repository")
   public void pluginRepoRemove(
            @Option(required = true, description = "repo url...",
                     completer = PluginRepositoryCompleter.class) final String url,
            final PipeOut out)
   {
      MavenPluginFacet deps = project.getFacet(MavenPluginFacet.class);

      DependencyRepository rep;
      if ((rep = deps.removePluginRepository(url)) != null)
      {
         out.println("Removed plugin repository [" + rep.getId() + "->" + rep.getUrl() + "]");
      }
      else
      {
         out.println("No plugin repository with url [" + url + "]");
      }
   }

   /*
    * Utils
    */
   private void printDep(final PipeOut out, final Dependency dep)
   {
      out.println(
               out.renderColor(ShellColor.BLUE, dep.getGroupId())
                        +
                        out.renderColor(ShellColor.BOLD, " : ")
                        +
                        out.renderColor(ShellColor.BLUE, dep.getArtifactId())
                        +
                        out.renderColor(ShellColor.BOLD, " : ")
                        +
                        out.renderColor(ShellColor.NONE, dep.getVersion() == null ? "" : dep.getVersion())
                        +
                        out.renderColor(ShellColor.BOLD, " : ")
                        +
                        out.renderColor(ShellColor.NONE, dep.getPackagingType() == null ? "" : dep
                                 .getPackagingType().toLowerCase())
                        +
                        out.renderColor(ShellColor.BOLD, " : ")
                        +
                        out.renderColor(determineDependencyShellColor(dep.getScopeTypeEnum()),
                                 dep.getScopeType() == null ? "compile" : dep.getScopeType()
                                          .toLowerCase()));
   }

   private ShellColor determineDependencyShellColor(final ScopeType type)
   {
      if (type == null)
      {
         return ShellColor.YELLOW;
      }
      switch (type)
      {
      case PROVIDED:
         return ShellColor.GREEN;
      case COMPILE:
         return ShellColor.YELLOW;
      case RUNTIME:
         return ShellColor.MAGENTA;
      case OTHER:
         return ShellColor.BLACK;
      case SYSTEM:
         return ShellColor.BLACK;
      case TEST:
         return ShellColor.BLUE;
      default:
         break;
      }
      return ShellColor.NONE;
   }

}
