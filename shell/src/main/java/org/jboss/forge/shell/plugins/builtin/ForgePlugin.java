/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.git.GitUtils;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.dependencies.CompositeDependencyFilter;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyFilter;
import org.jboss.forge.project.dependencies.DependencyQuery;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.PluginEntry;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.Wait;
import org.jboss.forge.shell.events.ReinitializeEnvironment;
import org.jboss.forge.shell.exceptions.AbortedException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.PluginManager;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.PluginRef;
import org.jboss.forge.shell.util.PluginUtil;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge")
@Topic("Shell Environment")
@Help("Forge control and writer environment commands. Manage plugins and other forge addons.")
public class ForgePlugin implements Plugin
{

   private final Event<ReinitializeEnvironment> reinitializeEvent;

   private final PluginManager pluginManager;
   private final DependencyResolver resolver;
   private final ForgeEnvironment environment;
   private final ShellPrompt prompt;
   private final Shell shell;
   private final Configuration configuration;

   @Inject
   private Wait wait;

   @Inject
   public ForgePlugin(final ForgeEnvironment environment, final Event<ReinitializeEnvironment> reinitializeEvent,
            final PluginManager pluginManager, final ShellPrompt prompt, final DependencyResolver resolver,
            final Shell shell, final Configuration configuration)
   {
      this.environment = environment;
      this.reinitializeEvent = reinitializeEvent;
      this.pluginManager = pluginManager;
      this.prompt = prompt;
      this.shell = shell;
      this.resolver = resolver;
      this.configuration = configuration;
   }

   /*
    * General methods
    */

   @DefaultCommand
   public void about(final PipeOut out)
   {
      out.println("    _____                    ");
      out.println("   |  ___|__  _ __ __ _  ___ ");
      out.println("   | |_ / _ \\| `__/ _` |/ _ \\  "
               + out.renderColor(ShellColor.YELLOW, "\\\\"));
      out.println("   |  _| (_) | | | (_| |  __/  "
               + out.renderColor(ShellColor.YELLOW, "//"));
      out.println("   |_|  \\___/|_|  \\__, |\\___| ");
      out.println("                   |___/      ");
      out.println("");
      out.print(ShellColor.ITALIC, "JBoss Forge");
      out.print(", version [ ");
      out.print(ShellColor.BOLD, environment.getRuntimeVersion());
      out.print(" ] - JBoss, by ");
      out.print(ShellColor.RED, "Red Hat, Inc.");
      out.println(" [ http://forge.jboss.org ]");
   }

   @Command(value = "restart", help = "Reload all plugins and default configurations")
   public void restart() throws Exception
   {
      reinitializeEvent.fire(new ReinitializeEnvironment());
   }

   @Command(value = "list-plugins", help = "List all installed plugin JAR files.")
   public void listInstalled(PipeOut out, String input)
   {
      List<PluginEntry> plugins = InstalledPluginRegistry.list();
      for (PluginEntry plugin : plugins)
      {
         if (Strings.isNullOrEmpty(input) || plugin.toString().contains(input))
         {
            out.println(plugin.toString());
         }
      }
   }

   /*
    * Plugin installation
    */

   @Command(value = "find-plugin",
            help = "Searches the configured Forge plugin index for a plugin matching the given search text")
   public void find(@Option(description = "search string") final String searchString, final PipeOut out)
            throws Exception
   {
      List<PluginRef> pluginList = PluginUtil.findPlugin(shell, configuration, searchString);

      if (!pluginList.isEmpty())
      {
         out.println();
      }
      for (PluginRef ref : pluginList)
      {
         out.println(" - " + out.renderColor(ShellColor.BOLD, ref.getName()) + " (" + ref.getArtifact() + ")");
         out.println("\tAuthor: " + ref.getAuthor());
         out.println("\tWebsite: " + ref.getWebsite());
         out.println("\tLocation: " + ref.getLocation());
         out.println("\tTags: " + ref.getTags());
         out.println("\tDescription: " + ref.getDescription());
         out.println();
      }
   }

   @Command(value = "remove-plugin",
            help = "Removes a plugin from the current Forge runtime configuration")
   public void removePlugin(
            @Option(completer = InstalledPluginCompleter.class, description = "plugin-name", required = true,
                     help = "The fully qualified plugin name e.g: 'org.jboss.forge.plugin:version'") final String pluginName,
            final PipeOut out) throws Exception
   {
      if (pluginManager.removePlugin(pluginName))
      {
         ShellMessages.success(out, "Successfully removed [" + pluginName + "]");
         restart();
      }
      else
      {
         ShellMessages.error(out, "Failed to remove [" + pluginName + "");
      }
   }

   @Command(value = "install-plugin",
            help = "Installs a plugin from the configured Forge plugin index")
   public void installFromIndex(
            @Option(description = "plugin-name", completer = IndexPluginNameCompleter.class, required = true) final String pluginName,
            @Option(name = "version", description = "branch, tag, or version to build") final String version,
            final PipeOut out) throws Exception
   {
      PluginRef plugin = PluginUtil.findPluginByName(shell, configuration, pluginName, true);

      if (plugin == null)
      {
         throw new RuntimeException("no plugin found with name [" + pluginName + "]");
      }
      else
      {
         ShellMessages.info(out, "Preparing to install plugin: " + plugin.getName());

         if (plugin.isGit())
         {
            installFromGit(plugin.getGitRepo(), Strings.isNullOrEmpty(version) ? plugin.getGitRef() : version, null,
                     false, plugin.getArtifact(),
                     out);
         }
         else
         {
            throw new UnsupportedOperationException("Not yet implemented");
         }
      }
   }

   @Command(value = "source-plugin",
            help = "Install a plugin from a local project folder")
   public void installFromLocalProject(
            @Option(description = "project directory", required = true) final Resource<?> projectFolder,
            @Option(name = "coordinates", type = PromptType.DEPENDENCY_ID, description = "the coordinates for the plugin (if in a multi-module repository)") final Dependency coordinates,
            final PipeOut out) throws Exception
   {
      DirectoryResource workspace = projectFolder.reify(DirectoryResource.class);
      if ((workspace == null) || !workspace.exists())
      {
         throw new IllegalArgumentException("Project folder must be specified.");
      }
      pluginManager.installFromProject(workspace, coordinates);

      ShellMessages.success(out, "Installed from [" + workspace + "] successfully.");
      restart();
   }

   @Command(value = "git-plugin",
            help = "Install a plugin from a public git repository")
   public void installFromGit(
            @Option(description = "git repo", required = true) final String gitRepo,
            @Option(name = "ref", description = "branch or tag to build") final String refName,
            @Option(name = "checkoutDir", description = "directory in which to clone the repository") final Resource<?> checkoutResource,
            @Option(name = "keepSources", description = "keep the sources after checking out", defaultValue = "false", flagOnly = true) final boolean keepSources,
            @Option(name = "coordinates", type = PromptType.DEPENDENCY_ID, description = "the coordinates for the plugin (if in a multi-module repository)") final Dependency coordinates,
            final PipeOut out) throws Exception
   {
      DirectoryResource buildDir;
      if (checkoutResource != null)
      {
         if (!(checkoutResource instanceof FileResource<?>))
         {
            throw new IllegalArgumentException("Checkout dir must be a directory path");
         }
         FileResource<?> checkoutDir = (FileResource<?>) checkoutResource;
         // Resource already exists
         if (checkoutDir.exists())
         {
            // Check if it is already a directory
            if (!checkoutDir.isDirectory())
            {
               throw new RuntimeException("Resource " + checkoutDir.getFullyQualifiedName()
                        + " is not a valid directory.");
            }
            buildDir = checkoutDir.reify(DirectoryResource.class);
            if (!shell.promptBoolean("Directory " + buildDir.getFullyQualifiedName()
                     + " already exists. Do you want to overwrite?", false))
            {
               throw new AbortedException("Directory " + buildDir.getFullyQualifiedName()
                        + " already exists");
            }
            buildDir.delete(true);
            buildDir.mkdirs();
         }
         else
         {
            // Resource does not exist. Create it
            checkoutDir.mkdirs();
            buildDir = checkoutDir.reify(DirectoryResource.class);
         }
      }
      else
      {
         buildDir = shell.getCurrentDirectory().createTempResource();
      }

      Git repo = null;
      try
      {
         ShellMessages.info(out, "Checking out plugin source files to [" + buildDir.getFullyQualifiedName()
                  + "] via 'git'");
         repo = GitUtils.clone(buildDir, gitRepo);

         Ref ref = null;
         String targetRef = refName;
         if (targetRef == null)
         {
            // Default to Forge runtime version if no Ref name is supplied.
            targetRef = environment.getRuntimeVersion();
         }

         if (targetRef != null)
         {
            // Try to find a Tag matching the given Ref name or runtime version
            Map<String, Ref> tags = repo.getRepository().getTags();
            ref = tags.get(targetRef);

            // Now try to find a matching Branch
            if (ref == null)
            {
               List<Ref> refs = GitUtils.getRemoteBranches(repo);
               for (Ref branchRef : refs)
               {
                  String branchName = branchRef.getName();
                  if (branchName != null && branchName.endsWith(targetRef))
                  {
                     ref = repo.branchCreate().setName(targetRef).setUpstreamMode(SetupUpstreamMode.TRACK)
                              .setStartPoint("origin/" + targetRef).call();
                  }
               }
            }

            // Now try to find a tag or branch with same Major.Minor.(x) version.
            if (ref == null)
            {
               // All
               List<String> sortedVersions = new ArrayList<String>();

               // Branches
               for (Ref branchRef : GitUtils.getRemoteBranches(repo))
               {
                  String branchName = branchRef.getName();
                  branchName = branchName.replaceFirst("refs/heads/", "");
                  if (InstalledPluginRegistry.isApiCompatible(targetRef, branchName))
                     sortedVersions.add(branchName);
               }

               // Tags

               // Branches
               for (String tag : tags.keySet())
               {
                  if (InstalledPluginRegistry.isApiCompatible(targetRef, tag))
                     sortedVersions.add(tag);
               }

               // Sort
               Collections.sort(sortedVersions);

               if (!sortedVersions.isEmpty())
               {
                  String version = sortedVersions.get(sortedVersions.size() - 1);
                  if (InstalledPluginRegistry.isApiCompatible(targetRef, version))
                  {
                     ref = tags.get(version);

                     if (ref == null)
                     {
                        ref = repo.branchCreate().setName(version).setUpstreamMode(SetupUpstreamMode.TRACK)
                                 .setStartPoint("origin/" + version).call();
                     }
                  }
               }
            }
         }

         if (ref == null)
         {
            ref = repo.getRepository().getRef("master");
         }

         if (ref != null)
         {
            ShellMessages.info(out, "Switching to branch/tag [" + ref.getName() + "]");
            GitUtils.checkout(repo, ref, false, SetupUpstreamMode.TRACK, false);
         }
         else if (refName != null)
         {
            throw new RuntimeException("Could not locate ref [" + targetRef + "] in repository ["
                     + repo.getRepository().getDirectory().getAbsolutePath() + "]");
         }
         else
         {
            ShellMessages.warn(
                     out,
                     "Could not find a Ref matching the current Forge version ["
                              + environment.getRuntimeVersion()
                              + "], building Plugin from HEAD.");
         }
         pluginManager.installFromProject(buildDir, coordinates);
      }
      finally
      {
         GitUtils.close(repo);
         if (buildDir != null)
         {
            if (keepSources)
            {
               ShellMessages.info(out,
                        "Sources are kept in [" + buildDir.getFullyQualifiedName() + "]");
            }
            else
            {
               ShellMessages.info(out,
                        "Cleaning up temp workspace [" + buildDir.getFullyQualifiedName()
                                 + "]");
               buildDir.delete(true);
            }
         }
      }

      ShellMessages.success(out, "Installed from [" + gitRepo + "] successfully.");
      restart();
   }

   /**
    * Aborts a forge update
    */
   @Command(value = "update-abort", help = "Aborts a previous forge update")
   public void updateAbort() throws IOException
   {
      DirectoryResource forgeHome = environment.getForgeHome();
      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      if (updateDirectory.exists())
      {
         if (updateDirectory.delete(true))
         {
            ShellMessages.success(shell,
                     "Update files were deleted. Run 'forge update' if you want to update this installation again.");
         }
         else
         {
            ShellMessages.info(shell, "Could not abort. Try to run 'forge update-abort' again");
         }
      }
      else
      {
         ShellMessages.info(shell, "No update files found");
      }
   }

   /**
    * Updates the forge version
    */
   @Command(value = "update", help = "Update this forge installation")
   public void update() throws IOException
   {
      if (environment.isEmbedded())
      {
         ShellMessages.warn(shell, "'forge update' only works when it is run outside of the IDE.");
         shell.println("The embedded Forge versions are automatically updated when there is a new version of the JBoss Tools Forge Plugin.");
         shell.println("If you want to use the latest version without waiting for a new plugin release, do the following:");
         shell.println();
         shell.println("1. Download the latest Forge version from http://forge.jboss.org and unzip in any place you may find convenient;");
         shell.println("2. In Window->Preferences, look for Forge->Installed Forge Runtimes and add the path to your installation and make it the default runtime choice;");
         shell.println("3. Start the Forge Console. You should see it is running the latest version.");
         return;
      }
      DirectoryResource forgeHome = environment.getForgeHome();
      DirectoryResource updateDir = forgeHome.getChildDirectory(".update");
      if (updateDir.exists())
      {
         ShellMessages
                  .warn(shell,
                           "There is an update pending. Restart Forge for the update to take effect. To abort this update, type 'forge update-abort'");
         return;
      }
      Dependency forgeDistribution = getLatestAvailableDistribution();
      if (forgeDistribution == null)
      {
         ShellMessages.info(shell, "Forge is up to date! Enjoy!");
      }
      else
      {
         shell.print(ShellColor.YELLOW, "***INFO*** ");
         shell.print("This Forge installation will be updated to ");
         shell.println(ShellColor.BOLD, forgeDistribution.getVersion());
         if (prompt.promptBoolean("Is that ok ?", true))
         {
            updateForge(forgeDistribution);
         }
      }
   }

   /**
    * Returns the latest available distribution
    * 
    * @return
    */
   private Dependency getLatestAvailableDistribution()
   {
      final String runtimeVersion = environment.getRuntimeVersion();
      DependencyQuery query = DependencyQueryBuilder.create(DependencyBuilder
               .create("org.jboss.forge:forge-distribution:::zip")).setFilter(
               new CompositeDependencyFilter(
                        new NonSnapshotDependencyFilter(),
                        new DependencyFilter()
                        {
                           @Override
                           public boolean accept(Dependency dependency)
                           {
                              // We are only interested in 1.x Final versions higher than the current version
                              // TODO: Consider the version string for 2.x and higher releases when porting.
                              String version = dependency.getVersion();
                              return version.compareTo(runtimeVersion) > 0 && version.startsWith("1.")
                                       && version.endsWith(".Final");
                           }
                        }
               )).setRepositories(new DependencyRepositoryImpl(DependencyFacet.KnownRepository.JBOSS_NEXUS));
      List<Dependency> versions = resolver.resolveVersions(query);
      return versions.isEmpty() ? null : versions.get(versions.size() - 1);
   }

   /**
    * Unpacks the dependency info a specific folder
    * 
    * @param dependency
    */
   private void updateForge(final Dependency dependency) throws IOException
   {
      wait.start("Update in progress. Please wait");
      List<DependencyResource> resolvedArtifacts = resolver.resolveArtifacts(dependency);
      Assert.isTrue(resolvedArtifacts.size() == 1, "Artifact was not found");
      DependencyResource resource = resolvedArtifacts.get(0);
      DirectoryResource forgeHome = environment.getForgeHome();
      Files.unzip(resource.getUnderlyingResourceObject(), forgeHome.getUnderlyingResourceObject());

      DirectoryResource childDirectory = forgeHome.getChildDirectory(dependency.getArtifactId() + "-"
               + dependency.getVersion());

      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      if (updateDirectory.exists())
      {
         updateDirectory.delete(true);
      }
      childDirectory.renameTo(updateDirectory);
      wait.stop();
      ShellMessages.success(shell, "Forge will now restart to complete the update...");
      System.exit(0);
   }
}