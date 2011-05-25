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

package org.jboss.forge.shell.plugins.builtin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.git.GitUtils;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.shell.PluginJar;
import org.jboss.forge.shell.PluginJar.IllegalNameException;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.ReinitializeEnvironment;
import org.jboss.forge.shell.exceptions.Abort;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PluginRef;
import org.jboss.forge.shell.util.PluginUtil;
import org.jboss.shrinkwrap.descriptor.impl.base.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge")
@Topic("Shell Environment")
@Help("Forge control and writer environment commands. Manage plugins and other forge addons.")
public class ForgePlugin implements Plugin
{
   private final Event<ReinitializeEnvironment> reinitializeEvent;
   private final ShellPrintWriter writer;
   private final DependencyResolver resolver;
   private final ForgeEnvironment environment;
   private final ShellPrompt prompt;
   private final Shell shell;

   @Inject
   public ForgePlugin(ForgeEnvironment environment, Event<ReinitializeEnvironment> reinitializeEvent,
            ShellPrintWriter writer, ShellPrompt prompt, DependencyResolver resolver, Shell shell)
   {
      this.environment = environment;
      this.reinitializeEvent = reinitializeEvent;
      this.writer = writer;
      this.prompt = prompt;
      this.shell = shell;
      this.resolver = resolver;
   }

   /*
    * General methods
    */

   @DefaultCommand
   public void about(PipeOut out)
   {
      out.println("   ____                          _____                    ");
      out.println("  / ___|  ___  __ _ _ __ ___    |  ___|__  _ __ __ _  ___ ");
      out.println("  \\___ \\ / _ \\/ _` | '_ ` _ \\   | |_ / _ \\| '__/ _` |/ _ \\  "
               + out.renderColor(ShellColor.YELLOW, "\\\\"));
      out.println("   ___) |  __/ (_| | | | | | |  |  _| (_) | | | (_| |  __/  "
               + out.renderColor(ShellColor.YELLOW, "//"));
      out.println("  |____/ \\___|\\__,_|_| |_| |_|  |_|  \\___/|_|  \\__, |\\___| ");
      out.println("                                                |___/      ");
      out.println("");
      String version = getClass().getPackage().getImplementationVersion();
      out.println("Seam Forge, version [ " + version + " ] - JBoss, by Red Hat, Inc. [ http://jboss.org ]");
   }

   @Command(value = "restart", help = "Reload all plugins and default configurations")
   public void restart() throws Exception
   {
      reinitializeEvent.fire(new ReinitializeEnvironment());
   }

   @Command(value = "list-plugins", help = "List all installed plugin JAR files.")
   public void listInstalled(
            @Option(name = "all",
                     shortName = "a",
                     description = "Show extra information about each installed plugin",
                     defaultValue = "false") boolean showAll)
   {
      DirectoryResource pluginDir = environment.getPluginDirectory();
      List<Resource<?>> list = pluginDir.listResources();
      List<Resource<?>> untracked = new ArrayList<Resource<?>>();
      List<PluginJar> installed = new ArrayList<PluginJar>();

      if (!list.isEmpty())
      {
         RES: for (Resource<?> res : list)
         {
            try
            {
               PluginJar jar = new PluginJar(res.getName());

               for (PluginJar p : installed)
               {
                  if (p.isSamePlugin(jar))
                  {
                     if (p.getVersion() < jar.getVersion())
                     {
                        installed.remove(p);
                        installed.add(jar);
                     }
                     continue RES;
                  }
               }
               installed.add(jar);
            }
            catch (IllegalNameException e)
            {
               untracked.add(res);
            }
         }

         if (!installed.isEmpty())
         {
            writer.println();
            writer.println(ShellColor.RED, "[installed plugins]");
            for (PluginJar jar : installed)
            {
               writer.print(ShellColor.ITALIC, jar.getDependency().getGroupId());
               writer.print(" : ");
               writer.print(ShellColor.BOLD, jar.getDependency().getArtifactId());
               writer.print(" : ");
               if (Strings.isNullOrEmpty(jar.getDependency().getVersion()))
               {
                  writer.print(ShellColor.RED, "unversioned");
               }
               else
               {
                  writer.print(ShellColor.YELLOW, jar.getDependency().getVersion());
               }

               if (showAll)
               {
                  writer.print(ShellColor.ITALIC,
                           " - " + environment.getPluginDirectory().getFullyQualifiedName() + "/" + jar.getFullName());
               }
               writer.println();
            }
         }

         if (!untracked.isEmpty())
         {
            writer.println();
            writer.println(ShellColor.RED, "[untracked plugins]");
            for (Resource<?> resource : untracked)
            {
               writer.println(" " + resource.getFullyQualifiedName());
            }
            writer.println();
         }
      }
   }

   /*
    * Plugin installation
    */

   @Command(value = "find-plugin",
            help = "Searches the configured Forge plugin index for a plugin matching the given search text")
   public void find(@Option(description = "search string") String searchString, final PipeOut out) throws Exception
   {
      // TODO remove this message once stabilized.
      ShellMessages.info(out, "This is a prototype feature and has limited functionality.");
      List<PluginRef> pluginList = PluginUtil.findPlugin(environment, searchString, out);

      for (PluginRef ref : pluginList)
      {
         out.println(" - " + out.renderColor(ShellColor.BOLD, ref.getName()) + " (" + ref.getArtifact() + ")");
      }
   }

   @Command(value = "install-plugin",
            help = "Installs a plugin from the configured Forge plugin index")
   public void installFromIndex(@Option(description = "plugin-name") String pluginName,
            final PipeOut out) throws Exception
   {
      // TODO remove this message once stabilized.
      ShellMessages.info(out, "This is a prototype feature and has limited functionality.");
      List<PluginRef> plugins = PluginUtil.findPlugin(environment, pluginName, out);

      if (plugins.isEmpty())
      {
         throw new RuntimeException("no plugin found with name [" + pluginName + "]");
      }
      else if (plugins.size() > 1)
      {
         throw new RuntimeException("ambiguous plugin query: multiple matches for [" + pluginName + "]");
      }
      else
      {
         PluginRef ref = plugins.get(0);
         ShellMessages.info(out, "Preparing to install plugin: " + ref.getName());

         if (!ref.isGit())
         {
            installFromMvnRepos(ref.getArtifact(), out, new DependencyRepositoryImpl("custom", ref.getHomeRepo()));
         }
         else if (ref.isGit())
         {
            installFromGit(ref.getGitRepo(), ref.getGitRef(), null, out);
         }
      }
   }

   private void installFromMvnRepos(Dependency dep, PipeOut out, final DependencyRepository... repoList)
            throws Exception
   {
      installFromMvnRepos(dep, out, Arrays.asList(repoList));
   }

   private void installFromMvnRepos(Dependency dep, PipeOut out, final List<DependencyRepository> repoList)
            throws Exception
   {
      List<DependencyResource> temp = resolver.resolveArtifacts(dep, repoList);
      List<DependencyResource> artifacts = new ArrayList<DependencyResource>();

      for (DependencyResource d : temp)
      {
         if (d.exists())
         {
            artifacts.add(d);
         }
      }

      DependencyResource artifact = null;
      if (artifacts.isEmpty())
      {
         throw new RuntimeException("No artifacts found for [" + dep + "]");
      }
      else if (artifacts.size() > 1)
      {
         artifact = prompt.promptChoiceTyped("Install which version?", artifacts, artifacts.get(artifacts.size() - 1));
      }
      else
      {
         artifact = artifacts.get(0);
      }
      FileResource<?> jar = createIncrementedPluginJarFile(artifact.getDependency());
      jar.setContents(artifact.getResourceInputStream());
      ShellMessages.success(out, "Installed from [" + dep.toCoordinates() + "] successfully.");

      restart();
   }

   @Command(value = "mvn-plugin",
            help = "Download and install a plugin from a maven repository")
   public void installFromMvnRepos(@Option(description = "plugin-identifier", required = true) Dependency dep,
            @Option(name = "knownRepo", description = "target repository") KnownRepository repo,
            @Option(name = "repoUrl", description = "target repository URL") String repoURL,
            final PipeOut out) throws Exception
   {
      if (repoURL != null)
      {
         installFromMvnRepos(dep, out,
                  new DependencyRepositoryImpl("custom", repoURL));
      }
      else if (repo == null)
      {
         List<DependencyRepository> repos = new ArrayList<DependencyRepository>();
         for (KnownRepository r : KnownRepository.values())
         {
            repos.add(new DependencyRepositoryImpl(r));
         }
         installFromMvnRepos(dep, out, repos);
      }
      else
         installFromMvnRepos(dep, out, new DependencyRepositoryImpl(repo));
   }

   @Command(value = "jar-plugin",
            help = "Install a plugin from a local project folder")
   public void installFromLocalJar(
            @Option(name = "jar", description = "jar file to install", required = true) Resource<?> resource,
            @Option(name = "id", description = "plugin identifier, [e.g. \"com.example.group : example-plugin\"]", required = true) Dependency dep,
            final PipeOut out) throws Exception
   {
      FileResource<?> source = resource.reify(FileResource.class);
      if (source == null || !source.exists())
      {
         throw new IllegalArgumentException("JAR file must be specified.");
      }

      if (environment.getPluginDirectory().equals(source.getParent()))
      {
         throw new IllegalArgumentException("Plugin is already installed.");
      }

      ShellMessages.info(out, "WARNING!");
      if (prompt.promptBoolean(
               "Installing plugins from remote sources is dangerous, and can leave untracked plugins. Continue?", true))
      {
         FileResource<?> target = createIncrementedPluginJarFile(dep);
         target.setContents(source.getResourceInputStream());

         ShellMessages.success(out, "Installed from [" + resource + "] successfully.");
         restart();
      }
      else
         throw new RuntimeException("Aborted.");
   }

   @Command(value = "url-plugin",
            help = "Download and install a plugin from the given URL")
   public void installFromRemoteURL(
            @Option(description = "URL of jar file", required = true) URL url,
            @Option(name = "id", description = "plugin identifier, [e.g. \"com.example.group : example-plugin\"]", required = true) Dependency dep,
            final PipeOut out) throws Exception
   {
      ShellMessages.info(out, "WARNING!");
      if (prompt.promptBoolean(
               "Installing plugins from remote sources is dangerous, and can leave untracked plugins. Continue?", true))
      {
         FileResource<?> jar = createIncrementedPluginJarFile(dep);
         PluginUtil.downloadFromURL(out, url, jar);
         ShellMessages.success(out, "Installed from [" + url.toExternalForm() + "] successfully.");
         restart();
      }
      else
         throw new RuntimeException("Aborted.");
   }

   @Command(value = "source-plugin",
            help = "Install a plugin from a local project folder")
   public void installFromLocalProject(
            @Option(description = "project directory", required = true) Resource<?> projectFolder,
            final PipeOut out) throws Exception
   {
      DirectoryResource workspace = projectFolder.reify(DirectoryResource.class);
      if (workspace == null || !workspace.exists())
      {
         throw new IllegalArgumentException("Project folder must be specified.");
      }

      buildFromCurrentProject(out, workspace);

      ShellMessages.success(out, "Installed from [" + workspace + "] successfully.");
      restart();
   }

   @Command(value = "git-plugin",
            help = "Install a plugin from a public git repository")
   public void installFromGit(
            @Option(description = "git repo", required = true) String gitRepo,
            @Option(name = "ref", description = "branch or tag to build") String ref,
            @Option(name = "checkoutDir", description = "directory in which to clone the repository") Resource<?> checkoutDir,
            final PipeOut out) throws Exception
   {

      DirectoryResource savedLocation = shell.getCurrentDirectory();
      DirectoryResource workspace = savedLocation.createTempResource();

      try
      {
         DirectoryResource buildDir = workspace.getChildDirectory("repo");
         if (checkoutDir != null)
         {
            if (!checkoutDir.exists() && checkoutDir instanceof FileResource<?>)
            {
               ((FileResource<?>) checkoutDir).mkdirs();
            }
            buildDir = checkoutDir.reify(DirectoryResource.class);
         }

         if (buildDir.exists())
         {
            buildDir.delete(true);
            buildDir.mkdir();
         }

         ShellMessages.info(out, "Checking out plugin source files to [" + buildDir.getFullyQualifiedName()
                  + "] via 'git'");
         Git repo = GitUtils.clone(buildDir, gitRepo);

         if (ref != null)
         {
            ShellMessages.info(out, "Switching to branch/tag [" + ref + "]");
            GitUtils.checkout(repo, ref, false, SetupUpstreamMode.SET_UPSTREAM, false);
         }

         buildFromCurrentProject(out, buildDir);
      }
      finally
      {
         if (checkoutDir != null)
         {
            ShellMessages.info(out,
                     "Cleaning up temp workspace [" + workspace.getFullyQualifiedName()
                              + "]");
            workspace.delete(true);
         }
      }

      ShellMessages.success(out, "Installed from [" + gitRepo + "] successfully.");
      restart();

   }

   /*
    * Helpers
    */
   private void buildFromCurrentProject(final PipeOut out, DirectoryResource buildDir) throws Abort
   {
      DirectoryResource savedLocation = shell.getCurrentDirectory();
      try
      {
         shell.setCurrentResource(buildDir);
         Project project = shell.getCurrentProject();
         if (project == null)
         {
            throw new IllegalStateException("Unable to recognise plugin project in ["
                     + buildDir.getFullyQualifiedName() + "]");
         }

         DependencyFacet deps = project.getFacet(DependencyFacet.class);
         if (!deps.hasDependency(DependencyBuilder.create("org.jboss.forge:forge-shell-api")) 
                  && !prompt.promptBoolean("The project does not appear to be a Forge Plugin Project, install anyway?",
                           false))
         {
            throw new Abort("Installation aborted");
         }

         ShellMessages.info(out, "Invoking build with underlying build system.");
         Resource<?> artifact = project.getFacet(PackagingFacet.class).executeBuild();
         if (artifact != null && artifact.exists())
         {
            MetadataFacet meta = project.getFacet(MetadataFacet.class);
            Dependency dep = meta.getOutputDependency();

            FileResource<?> jar = createIncrementedPluginJarFile(dep);

            ShellMessages.info(out, "Installing plugin artifact.");
            jar.setContents(artifact.getResourceInputStream());
         }
         else
         {
            throw new IllegalStateException("Build artifact [" + artifact.getFullyQualifiedName()
                        + "] is missing and cannot be installed. Please resolve build errors and try again.");
         }
      }
      finally
      {
         shell.setCurrentResource(savedLocation);
      }
   }

   private FileResource<?> createIncrementedPluginJarFile(Dependency dep)
   {
      int version = 0;
      PluginJar pluginJar = new PluginJar(dep);
      DirectoryResource pluginDir = environment.getPluginDirectory();
      List<Resource<?>> list = pluginDir.listResources(new StartsWith(pluginJar.getName()));

      if (list.size() > 0 && !prompt.promptBoolean(
                        "An existing version of this plugin was found. Replace it?", true))
      {
         throw new RuntimeException("Aborted.");
      }

      for (Resource<?> res : list)
      {
         PluginJar jar = new PluginJar(res.getName());
         if (jar.getVersion() > version)
         {
            version = jar.getVersion();
         }
         if (res instanceof FileResource<?>)
            ((FileResource<?>) res).deleteOnExit();
      }

      String finalName = new PluginJar(dep, version + 1).getFullName();
      FileResource<?> jar = (FileResource<?>) pluginDir.getChild(finalName);
      jar.createNewFile();
      return jar;
   }

   private class StartsWith implements ResourceFilter
   {
      private final String prefix;

      public StartsWith(String prefix)
      {
         this.prefix = prefix;
      }

      @Override
      public boolean accept(Resource<?> resource)
      {
         return resource != null && resource.getName() != null && resource.getName().startsWith(prefix);
      }

   }
}
