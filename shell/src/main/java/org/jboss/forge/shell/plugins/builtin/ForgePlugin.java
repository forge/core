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
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.DependencyFacet.KnownRepository;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
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
import org.jboss.shrinkwrap.descriptor.spi.Node;

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
   public ForgePlugin(final ForgeEnvironment environment, final Event<ReinitializeEnvironment> reinitializeEvent,
            final ShellPrintWriter writer, final ShellPrompt prompt, final DependencyResolver resolver,
            final Shell shell)
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
   public void about(final PipeOut out)
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
                     defaultValue = "false") final boolean showAll)
   {
      throw new IllegalStateException("Not implemented.");
   }

   /*
    * Plugin installation
    */

   @Command(value = "find-plugin",
            help = "Searches the configured Forge plugin index for a plugin matching the given search text")
   public void find(@Option(description = "search string") final String searchString, final PipeOut out)
            throws Exception
   {
      List<PluginRef> pluginList = PluginUtil.findPlugin(environment, searchString, out);

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

   @Command(value = "install-plugin",
            help = "Installs a plugin from the configured Forge plugin index")
   public void installFromIndex(@Option(description = "plugin-name") final String pluginName,
            final PipeOut out) throws Exception
   {
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

   private void installFromMvnRepos(final Dependency dep, final PipeOut out, final DependencyRepository... repoList)
            throws Exception
   {
      installFromMvnRepos(dep, out, Arrays.asList(repoList));
   }

   private void installFromMvnRepos(final Dependency dep, final PipeOut out, final List<DependencyRepository> repoList)
            throws Exception
   {
      throw new IllegalStateException("Not implemented");

      /*
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
      //FIXME this needs to be made to work with modules
      FileResource<?> jar = createIncrementedPluginJarFile(artifact.getDependency());
      jar.setContents(artifact.getResourceInputStream());
      ShellMessages.success(out, "Installed from [" + dep.toCoordinates() + "] successfully.");

      restart();
      */
   }

   // @Command(value = "mvn-plugin",
   // help = "Download and install a plugin from a maven repository")
   public void installFromMvnRepos(@Option(description = "plugin-identifier", required = true) final Dependency dep,
            @Option(name = "knownRepo", description = "target repository") final KnownRepository repo,
            @Option(name = "repoUrl", description = "target repository URL") final String repoURL,
            final PipeOut out) throws Exception
   {
      throw new IllegalStateException("Not implemented");
      /*
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
         */
   }

   // @Command(value = "jar-plugin",
   // help = "Install a plugin from a local project folder")
   public void installFromLocalJar(
            @Option(name = "jar", description = "jar file to install", required = true) final Resource<?> resource,
            @Option(name = "id", description = "plugin identifier, [e.g. \"com.example.group : example-plugin\"]", required = true) final Dependency dep,
            final PipeOut out) throws Exception
   {

      throw new IllegalStateException("Not implemented");
      /*
      FileResource<?> source = resource.reify(FileResource.class);
      if ((source == null) || !source.exists())
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
         */
   }

   // @Command(value = "url-plugin",
   // help = "Download and install a plugin from the given URL")
   public void installFromRemoteURL(
            @Option(description = "URL of jar file", required = true) final URL url,
            @Option(name = "id", description = "plugin identifier, [e.g. \"com.example.group : example-plugin\"]", required = true) final Dependency dep,
            final PipeOut out) throws Exception
   {

      throw new IllegalStateException("Not implemented");
      /*
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
         */
   }

   @Command(value = "source-plugin",
            help = "Install a plugin from a local project folder")
   public void installFromLocalProject(
            @Option(description = "project directory", required = true) final Resource<?> projectFolder,
            final PipeOut out) throws Exception
   {
      DirectoryResource workspace = projectFolder.reify(DirectoryResource.class);
      if ((workspace == null) || !workspace.exists())
      {
         throw new IllegalArgumentException("Project folder must be specified.");
      }

      buildFromCurrentProject(out, workspace);

      ShellMessages.success(out, "Installed from [" + workspace + "] successfully.");
      // restart();
   }

   @Command(value = "git-plugin",
            help = "Install a plugin from a public git repository")
   public void installFromGit(
            @Option(description = "git repo", required = true) final String gitRepo,
            @Option(name = "ref", description = "branch or tag to build") final String ref,
            @Option(name = "checkoutDir", description = "directory in which to clone the repository") final Resource<?> checkoutDir,
            final PipeOut out) throws Exception
   {

      DirectoryResource savedLocation = shell.getCurrentDirectory();
      DirectoryResource workspace = savedLocation.createTempResource();

      try
      {
         DirectoryResource buildDir = workspace.getChildDirectory("repo");
         if (checkoutDir != null)
         {
            if (!checkoutDir.exists() && (checkoutDir instanceof FileResource<?>))
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
      // restart();

   }

   /*
    * Helpers
    */
   private void buildFromCurrentProject(final PipeOut out, final DirectoryResource buildDir) throws Abort
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
         if ((artifact != null) && artifact.exists())
         {
            MetadataFacet meta = project.getFacet(MetadataFacet.class);
            Dependency dep = meta.getOutputDependency();

            ShellMessages.info(out, "Installing plugin artifact.");
            DirectoryResource module = createModule(project, dep, artifact);
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

   private DirectoryResource createModule(final Project project, final Dependency dep, final Resource<?> artifact)
   {
      DirectoryResource moduleDir = getOrCreatePluginModuleDirectory(dep);
      String pluginName = dep.getGroupId() + "." + dep.getArtifactId();
      String pluginSlot = dep.getVersion();

      FileResource<?> moduleXml = (FileResource<?>) moduleDir.getChild("module.xml");
      if (moduleXml.exists()
               && !prompt.promptBoolean(
                        "An existing installation for version [" + pluginSlot
                                 + "] of this plugin was found. Replace it?", true))
      {
         throw new RuntimeException("Aborted.");
      }

      moduleXml.delete();
      moduleXml.createNewFile();

      // <resource-root path="maven-dependency.jar" />
      Node module = XMLParser.parse(getClass().getResourceAsStream("/org/jboss/forge/modules/module.xml"));
      module.attribute("name", pluginName);
      module.attribute("slot", pluginSlot);
      Node resources = module.getSingle("resources");

      resources.create("resource-root").attribute("path", dep.getArtifactId() + ".jar");
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      // Copy the compiled JAR into the module directory
      FileResource<?> jar = moduleDir.getChild(dep.getArtifactId() + ".jar").reify(FileResource.class);
      jar.createNewFile();
      jar.setContents(artifact.getResourceInputStream());

      List<DependencyResource> pluginDependencies = new ArrayList<DependencyResource>();
      for (Dependency d : deps.getEffectiveDependenciesInScopes(ScopeType.COMPILE, ScopeType.RUNTIME)) {
         if (d.getPackagingTypeEnum().equals(PackagingType.JAR)
                  && !d.getGroupId().equals("org.jboss.forge"))
         {
            List<DependencyResource> artifacts = resolver.resolveArtifacts(d);
            if (artifacts.size() != 1)
            {
               // throw new RuntimeException("Oops! Wrong number of artifacts; we need 1 but found ["
               // + artifacts.size() + "]");
            }
            else
               pluginDependencies.addAll(artifacts);
         }
         // TODO encapsulate this?
         if (DependencyBuilder.areEquivalent(d, DependencyBuilder.create("org.jboss.forge:forge-javaee-api")))
         {
            module.getSingle("dependencies").create("module")
                     .attribute("name", "org.jboss.forge.javaee-api");
         }
      }

      // Copy dependencies into module
      for (DependencyResource d : pluginDependencies) {
         String name = d.getName();
         Resource<?> child = moduleDir.getChild(name);
         child.delete();
         FileResource<?> depJar = child.reify(FileResource.class);
         depJar.setContents(d.getResourceInputStream());
         resources.create("resource-root").attribute("path", name);
      }

      // <module name="org.jboss.forge:main" />
      Node dependencies = module.getSingle("dependencies");
      dependencies.create("module").attribute("name", "org.jboss.forge.shell-api");
      dependencies.create("module").attribute("name", "javax.api");

      moduleXml.setContents(XMLParser.toXMLString(module));

      // Add to list modules.
      registerPlugin(pluginName, pluginSlot);

      return moduleDir;
   }

   public void registerPlugin(final String pluginName, final String pluginSlot)
   {

      DirectoryResource dir = environment.getPluginDirectory();
      dir = dir.getOrCreateChildDirectory("org");
      dir = dir.getOrCreateChildDirectory("jboss");
      dir = dir.getOrCreateChildDirectory("forge");
      dir = dir.getOrCreateChildDirectory("plugins");
      dir = dir.getOrCreateChildDirectory("main");
      FileResource<?> moduleXml = dir.getChild("module.xml").reify(FileResource.class);

      if (!moduleXml.exists() || moduleXml.isDirectory())
      {
         moduleXml.delete(true);
         moduleXml.createNewFile();
         moduleXml.setContents(getClass().getResourceAsStream("/org/jboss/forge/modules/module.xml"));
      }

      Node module = XMLParser.parse(moduleXml.getResourceInputStream());

      Node plugin = module.attribute("name", "org.jboss.forge.plugins")
               .getSingle("dependencies")
               .getOrCreate("module@name=" + pluginName);

      plugin.attribute("slot", pluginSlot)
               .attribute("export", true).attribute("services", "export")
               .attribute("optional", "true");

      Node imports = plugin.getOrCreate("imports");
      imports.getOrCreate("include@path=**");
      imports.getOrCreate("include@path=META-INF");

      Node exports = plugin.getOrCreate("exports").getOrCreate("include-set");
      exports.getOrCreate("path@name=**");
      exports.getOrCreate("path@name=META-INF");

      moduleXml.setContents(XMLParser.toXMLString(module));
   }

   public DirectoryResource getOrCreatePluginModuleDirectory(final Dependency dep)
   {
      DirectoryResource pluginDir = environment.getPluginDirectory();

      List<String> groupId = Arrays.asList(dep.getGroupId().split("\\."));
      List<String> artifactId = Arrays.asList(dep.getArtifactId().split("\\."));
      DirectoryResource dir = pluginDir;
      for (String segment : groupId) {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      for (String segment : artifactId) {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      dir = dir.getOrCreateChildDirectory(dep.getVersion());
      return dir;
   }

   public DirectoryResource getOrCreatePluginDependenciesModuleDirectory(final Dependency dep)
   {
      DirectoryResource pluginDir = environment.getPluginDirectory();

      List<String> groupId = Arrays.asList(dep.getGroupId().split("\\."));
      List<String> artifactId = Arrays.asList(dep.getArtifactId().split("\\."));
      DirectoryResource dir = pluginDir;
      for (String segment : groupId) {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      for (String segment : artifactId) {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      dir = dir.getOrCreateChildDirectory("dependencies");
      dir = dir.getOrCreateChildDirectory(dep.getVersion());
      return dir;
   }
}
