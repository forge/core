/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.git.GitUtils;
import org.jboss.forge.parser.ParserException;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.CompositeDependencyFilter;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyFilter;
import org.jboss.forge.project.dependencies.DependencyQuery;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.PluginEntry;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.Wait;
import org.jboss.forge.shell.events.PluginInstalled;
import org.jboss.forge.shell.events.PluginRemoved;
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
import org.jboss.forge.shell.util.Files;
import org.jboss.forge.shell.util.ForgeProxySelector;
import org.jboss.forge.shell.util.PluginRef;
import org.jboss.forge.shell.util.PluginUtil;
import org.jboss.forge.shell.util.ProxySettings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge")
@Topic("Shell Environment")
@Help("Forge control and writer environment commands. Manage plugins and other forge addons.")
public class ForgePlugin implements Plugin
{

   private static final String MODULE_TEMPLATE_XML = "/org/jboss/forge/modules/module-template.xml";
   private final Event<ReinitializeEnvironment> reinitializeEvent;

   private final Event<PluginInstalled> pluginInstalledEvent;
   private final Event<PluginRemoved> pluginRemovedEvent;
   private final ShellPrintWriter writer;
   private final DependencyResolver resolver;
   private final ForgeEnvironment environment;
   private final ShellPrompt prompt;
   private final Shell shell;
   private final Configuration configuration;

   @Inject
   private Wait wait;

   @Inject
   public ForgePlugin(final ForgeEnvironment environment, final Event<ReinitializeEnvironment> reinitializeEvent,
            final ShellPrintWriter writer, final ShellPrompt prompt, final DependencyResolver resolver,
            final Shell shell, final Configuration configuration, final Event<PluginInstalled> pluginInstalledEvent,
            final Event<PluginRemoved> pluginRemovedEvent)
   {
      this.environment = environment;
      this.reinitializeEvent = reinitializeEvent;
      this.writer = writer;
      this.prompt = prompt;
      this.shell = shell;
      this.resolver = resolver;
      this.configuration = configuration;
      this.pluginInstalledEvent = pluginInstalledEvent;
      this.pluginRemovedEvent = pluginRemovedEvent;
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
      out.print(ShellColor.RED,"Red Hat, Inc.");
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

      PluginEntry plugin = PluginEntry.fromCoordinates(pluginName);
      if (!InstalledPluginRegistry.has(plugin))
      {
         throw new RuntimeException("No such installed plugin [" + pluginName + "]");
      }
      PluginEntry installedPlugin = InstalledPluginRegistry.get(plugin);
      InstalledPluginRegistry.remove(installedPlugin);
      pluginRemovedEvent.fire(new PluginRemoved(installedPlugin));
      if (!InstalledPluginRegistry.has(plugin))
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
            @Option(description = "plugin-name", completer = IndexPluginNameCompleter.class) final String pluginName,
            @Option(name = "version", description = "branch, tag, or version to build") final String version,
            final PipeOut out) throws Exception
   {
      List<PluginRef> plugins = PluginUtil.findPlugin(shell, configuration, pluginName);

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

         if (ref.isGit())
         {
            installFromGit(ref.getGitRepo(), Strings.isNullOrEmpty(version) ? ref.getGitRef() : version, null, out);
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
            final PipeOut out) throws Exception
   {
      DirectoryResource workspace = projectFolder.reify(DirectoryResource.class);
      if ((workspace == null) || !workspace.exists())
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
            @Option(description = "git repo", required = true) final String gitRepo,
            @Option(name = "ref", description = "branch or tag to build") final String refName,
            @Option(name = "checkoutDir", description = "directory in which to clone the repository") final Resource<?> checkoutDir,
            final PipeOut out) throws Exception
   {

      DirectoryResource workspace = shell.getCurrentDirectory().createTempResource();

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

         prepareProxyForJGit();

         ShellMessages.info(out, "Checking out plugin source files to [" + buildDir.getFullyQualifiedName()
                  + "] via 'git'");
         Git repo = GitUtils.clone(buildDir, gitRepo);

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
                              // We are only interested in versions higher than the current version
                              return dependency.getVersion().compareTo(runtimeVersion) > 0;
                           }
                        }
               ));
      List<Dependency> versions = resolver.resolveVersions(query);
      return versions.isEmpty() ? null : versions.get(versions.size() - 1);
   }

   /**
    * Unpacks the dependency info a specific folder
    *
    * @param dependency
    */
   private void updateForge(Dependency dependency) throws IOException
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

   private void prepareProxyForJGit()
   {
      ProxySettings proxySettings = ProxySettings.fromForgeConfiguration(configuration);
      if (proxySettings == null)
      {
         // There is no proxy configured
         return;
      }
      if (!(ProxySelector.getDefault() instanceof ForgeProxySelector))
      {
         ForgeProxySelector selector = new ForgeProxySelector(ProxySelector.getDefault(),
                  proxySettings);
         ProxySelector.setDefault(selector);
      }
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
         DependencyBuilder shellApi = DependencyBuilder.create("org.jboss.forge:forge-shell-api");

         String apiVersion = null;
         if (!deps.hasEffectiveDependency(shellApi)
                  && !prompt.promptBoolean(
                           "The project does not appear to be a Forge Plugin Project, install anyway?",
                           false))
         {
            throw new Abort("Installation aborted");
         }
         else
         {
            if (apiVersion == null)
            {
               Dependency directDependency = deps.getDirectDependency(shellApi);
               if ((directDependency != null) && !Strings.isNullOrEmpty(directDependency.getVersion()))
                  apiVersion = directDependency.getVersion();
            }

            if (apiVersion == null)
            {
               // Fall back to checking managed dependencies for a version
               Dependency managedDependency = deps.getManagedDependency(shellApi);
               if ((managedDependency != null) && !Strings.isNullOrEmpty(managedDependency.getVersion()))
                  apiVersion = managedDependency.getVersion();
            }

            if (apiVersion == null)
            {
               // Now completely give up and just use the result from the build
               Dependency effectiveDependency = deps.getEffectiveDependency(shellApi);
               if (effectiveDependency != null)
                  apiVersion = effectiveDependency.getVersion();
               else
                  apiVersion = environment.getRuntimeVersion();
            }
         }

         /**
          * Make sure that our PROVIDED modules are not included in the module dependencies
          */
         // TODO Weld bug requires us to correct /add module for Seam Render dependency
         List<String> groupIds = Arrays.asList("org.jboss.seam.render", "org.jboss.forge");
         List<String> providedDeps = Arrays.asList("forge-javaee-api", "forge-maven-api", "forge-scaffold-api",
                  "forge-shell-api");
         List<Dependency> dependencies = deps.getDependencies();
         for (Dependency dependency : dependencies)
         {
            if (groupIds.contains(dependency.getGroupId())
                     && !(ScopeType.PROVIDED.equals(dependency.getScopeTypeEnum())
                     || ScopeType.TEST.equals(dependency.getScopeTypeEnum())))
            {
               ShellMessages.warn(out, "Dependency [" + dependency.toCoordinates()
                        + "] was not correctly marked as PROVIDED scope; this has been corrected.");
               deps.addDirectDependency(DependencyBuilder.create(dependency).setScopeType(ScopeType.PROVIDED));
            }
            else if (dependency.getGroupId().equals("org.jboss.forge")
                     && !providedDeps.contains(dependency.getArtifactId())
                     && !ScopeType.TEST.equals(deps.getEffectiveDependency(dependency).getScopeTypeEnum()))
            {
               ShellMessages.warn(writer,
                        "Plugin has a dependency on internal Forge API [" + dependency
                                 + "] - this is not allowed and may cause failures.");
            }
         }

         ShellMessages.info(out, "Invoking build with underlying build system.");
         Resource<?> artifact = project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build();
         if ((artifact != null) && artifact.exists())
         {
            MetadataFacet meta = project.getFacet(MetadataFacet.class);
            Dependency dep = meta.getOutputDependency();

            ShellMessages.info(out, "Installing plugin artifact.");

            // TODO Figure out a better plugin versioning strategy than random numbers, also see if unloading is
            // possible to avoid this entirely.
            createModule(
                     project,
                     DependencyBuilder.create(dep).setVersion(
                              dep.getVersion() + "-" + UUID.randomUUID().toString()),
                     artifact, apiVersion);
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

   private boolean needDependenciesAsResourceRoot(final Project project)
   {
      FileResource<?> forgeXml = (FileResource<?>) project.getProjectRoot().getChild(
               "src/main/resources/META-INF/forge.xml");
      if (forgeXml.exists())
      {
         try
         {
            Node node = XMLParser.parse(forgeXml.getResourceInputStream());
            return node.getSingle("dependencies-as-resource-root") != null;
         }
         catch (ParserException e)
         {
            return false;
         }
      }
      return false;
   }

   private DirectoryResource createModule(final Project project, final Dependency dep, final Resource<?> artifact,
            final String apiVersion)
   {

      boolean dependenciesAsResourceRoot = needDependenciesAsResourceRoot(project);

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
      Node module = XMLParser.parse(getClass().getResourceAsStream(MODULE_TEMPLATE_XML));
      module.attribute("name", pluginName);
      module.attribute("slot", pluginSlot);
      Node resources = module.getSingle("resources");

      resources.createChild("resource-root").attribute("path", dep.getArtifactId() + ".jar");

      if (dependenciesAsResourceRoot)
      {
         writeResourceRoots(project, module, moduleDir, resources);
      }

      // Copy the compiled JAR into the module directory
      FileResource<?> jar = moduleDir.getChild(dep.getArtifactId() + ".jar").reify(FileResource.class);
      jar.createNewFile();
      jar.setContents(artifact.getResourceInputStream());

      // <module name="org.jboss.forge:main" />
      Node dependencies = module.getSingle("dependencies");

      if (!dependenciesAsResourceRoot)
      {
         dependencies.createChild("module").attribute("name", pluginName + ".dependencies")
                  .attribute("slot", pluginSlot);
      }

      dependencies.createChild("module").attribute("name", "org.jboss.forge.javaee.api")
               .attribute("services", "import");
      dependencies.createChild("module").attribute("name", "org.jboss.forge.maven.api")
               .attribute("services", "import");
      dependencies.createChild("module").attribute("name", "org.jboss.forge.scaffold.api")
               .attribute("services", "import");
      dependencies.createChild("module").attribute("name", "org.jboss.forge.shell.api")
               .attribute("services", "import");
      dependencies.createChild("module").attribute("name", "org.jboss.seam.render").attribute("services", "import");
      dependencies.createChild("module").attribute("name", "javax.api");

      moduleXml.setContents(XMLParser.toXMLString(module));

      if (!dependenciesAsResourceRoot)
      {
         createDependenciesModule(project, dep);
      }

      // Add to list modules.
      registerPlugin(pluginName, pluginSlot, apiVersion);

      return moduleDir;
   }

   private List<DependencyResource> getPluginDependencies(final Project project, Node module)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      List<DependencyResource> pluginDependencies = new ArrayList<DependencyResource>();
      List<Dependency> effectiveDependenciesInScopes = deps.getEffectiveDependenciesInScopes(ScopeType.COMPILE,
               ScopeType.RUNTIME);
      for (Dependency d : effectiveDependenciesInScopes)
      {
         if (d.getPackagingTypeEnum().equals(PackagingType.JAR)
                  && !d.getGroupId().equals("org.jboss.forge"))
         {
            List<DependencyResource> artifacts = resolveArtifacts(project, d);
            pluginDependencies.addAll(artifacts);
         }
         // TODO encapsulate this?
         if (DependencyBuilder.areEquivalent(d, DependencyBuilder.create("org.jboss.forge:forge-javaee-api")))
         {
            module.getSingle("dependencies").createChild("module")
                     .attribute("name", "org.jboss.forge.javaee.api")
                     .attribute("services", "import");
         }
         else if (DependencyBuilder.areEquivalent(d, DependencyBuilder.create("org.jboss.forge:forge-scaffold-api")))
         {
            module.getSingle("dependencies").createChild("module")
                     .attribute("name", "org.jboss.forge.scaffold.api")
                     .attribute("services", "import");
         }
         else if (DependencyBuilder.areEquivalent(d, DependencyBuilder.create("org.jboss.forge:forge-maven-api")))
         {
            module.getSingle("dependencies").createChild("module")
                     .attribute("name", "org.jboss.forge.maven.api")
                     .attribute("services", "import");
         }
         else if (d.getGroupId().equals("org.jboss.forge"))
         {
            ShellMessages.error(writer,
                     "Plugin has a dependency on internal Forge API [" + d
                              + "] - this is not allowed and may cause failures.");
         }
      }
      return pluginDependencies;
   }

   private void writeResourceRoots(
            final Project project,
            final Node module,
            final DirectoryResource directory,
            final Node resources)
   {
      List<DependencyResource> pluginDependencies = getPluginDependencies(project, module);
      for (DependencyResource d : pluginDependencies)
      {
         String name = d.getName();
         Resource<?> child = directory.getChild(name);
         child.delete();
         FileResource<?> depJar = child.reify(FileResource.class);
         depJar.setContents(d.getResourceInputStream());
         resources.createChild("resource-root").attribute("path", name);
      }
   }

   private void createDependenciesModule(final Project project, final Dependency dep)
   {
      DirectoryResource dependencyDir = getOrCreatePluginDependenciesModuleDirectory(dep);
      String pluginName = dep.getGroupId() + "." + dep.getArtifactId();
      String pluginSlot = dep.getVersion();

      FileResource<?> moduleXml = (FileResource<?>) dependencyDir.getChild("module.xml");
      moduleXml.delete();
      moduleXml.createNewFile();

      // <resource-root path="maven-dependency.jar" />
      Node module = XMLParser.parse(getClass().getResourceAsStream(MODULE_TEMPLATE_XML));
      module.attribute("name", pluginName + ".dependencies");
      module.attribute("slot", pluginSlot);
      Node resources = module.getSingle("resources");

      // <module name="org.jboss.forge:main" />
      Node dependencies = module.getSingle("dependencies");
      dependencies.createChild("module").attribute("name", "javax.api");
      dependencies.createChild("module").attribute("name", "org.jboss.forge.shell.api");

      writeResourceRoots(project, module, dependencyDir, resources);

      // Write out the module XML file.
      moduleXml.setContents(XMLParser.toXMLString(module));
   }

   private List<DependencyResource> resolveArtifacts(final Project project, final Dependency dep)
   {
      Dependency d = dep;

      List<DependencyResource> artifacts = new ArrayList<DependencyResource>();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      for (Dependency d2 : deps.getDependencies())
      {
         if (DependencyBuilder.areEquivalent(d, d2) && (d2.getVersion() != null))
         {
            d = d2;
            break;
         }
      }

      if (artifacts.size() != 1)
      {
         artifacts = resolver.resolveArtifacts(d, deps.getRepositories());
      }
      if (artifacts.size() != 1)
      {
         ShellMessages.warn(writer, "Could not resolve dependency [" + d.toCoordinates() + "]");
      }

      return artifacts;
   }

   public void registerPlugin(final String pluginName, final String pluginSlot, final String apiVersion)
   {
      String runtimeVersion = InstalledPluginRegistry.getRuntimeAPIVersion();
      if (InstalledPluginRegistry.isApiCompatible(runtimeVersion, apiVersion))
      {
         PluginEntry entry = InstalledPluginRegistry.install(pluginName, apiVersion, pluginSlot);
         pluginInstalledEvent.fire(new PluginInstalled(entry));
      }
      else
      {
         throw new RuntimeException(
                  "Could not install plugin ["
                           + pluginName
                           + "] because it references Forge API version ["
                           + apiVersion
                           + "] which may not be compatible with my current version ["
                           + runtimeVersion
                           + "]. Please consider upgrading forge, by typing 'forge update'. Otherwise, try installing an older version of the plugin.");
      }
   }

   public DirectoryResource getOrCreatePluginModuleDirectory(final Dependency dep)
   {
      DirectoryResource pluginDir = environment.getPluginDirectory();

      List<String> groupId = Arrays.asList(dep.getGroupId().split("\\."));
      List<String> artifactId = Arrays.asList(dep.getArtifactId().split("\\."));
      DirectoryResource dir = pluginDir;
      for (String segment : groupId)
      {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      for (String segment : artifactId)
      {
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
      for (String segment : groupId)
      {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      for (String segment : artifactId)
      {
         dir = dir.getOrCreateChildDirectory(segment);
      }

      dir = dir.getOrCreateChildDirectory("dependencies");
      dir = dir.getOrCreateChildDirectory(dep.getVersion());
      return dir;
   }
}
