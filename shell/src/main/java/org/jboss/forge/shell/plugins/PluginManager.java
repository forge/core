/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.ParserException;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DependencyResource;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.PluginEntry;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.events.PluginInstalled;
import org.jboss.forge.shell.events.PluginRemoved;
import org.jboss.forge.shell.exceptions.Abort;

/**
 * Manages the installation and removal of plugins
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class PluginManager
{
   private static final String MODULE_TEMPLATE_XML = "/org/jboss/forge/modules/module-template.xml";
   private static final Dependency SHELL_API = DependencyBuilder.create("org.jboss.forge:forge-shell-api");

   @Inject
   private Event<PluginInstalled> pluginInstalledEvent;

   @Inject
   private Event<PluginRemoved> pluginRemovedEvent;

   @Inject
   private ForgeEnvironment environment;

   @Inject
   private Shell shell;

   @Inject
   private DependencyResolver resolver;

   @Inject
   private ProjectFactory projectFactory;

   /**
    * Called when a plugin needs to be removed
    * 
    * @param pluginName
    * @return
    */
   public boolean removePlugin(final String pluginName)
   {
      PluginEntry plugin = PluginEntry.fromCoordinates(pluginName);
      if (!InstalledPluginRegistry.has(plugin))
      {
         throw new RuntimeException("No such installed plugin [" + pluginName + "]");
      }
      PluginEntry installedPlugin = InstalledPluginRegistry.get(plugin);
      InstalledPluginRegistry.remove(installedPlugin);
      pluginRemovedEvent.fire(new PluginRemoved(installedPlugin));
      return (!InstalledPluginRegistry.has(plugin));
   }

   /**
    * Installs from a specific project
    * 
    * @param buildDir
    * @param coordinates The maven coordinates information of the required plugin
    * @throws Abort
    */
   public void installFromProject(final DirectoryResource buildDir, final Dependency coordinates) throws Abort
   {
      DirectoryResource root = projectFactory.findProjectRootRecusively(buildDir);
      Project rootProject = projectFactory.findProject(root);
      if (rootProject == null)
      {
         throw new IllegalArgumentException("Unable to recognise plugin project in ["
                  + buildDir.getFullyQualifiedName() + "]");
      }
      Project pluginProject = findPluginProject(rootProject, coordinates);
      if (pluginProject == null)
      {
         throw new Abort("The project does not contain a valid Forge Plugin project. Installation aborted");
      }

      DependencyFacet deps = pluginProject.getFacet(DependencyFacet.class);

      String apiVersion = null;
      Dependency directDependency = deps.getDirectDependency(SHELL_API);
      if ((directDependency != null) && !Strings.isNullOrEmpty(directDependency.getVersion()))
         apiVersion = directDependency.getVersion();

      if (apiVersion == null)
      {
         // Fall back to checking managed dependencies for a version
         Dependency managedDependency = deps.getManagedDependency(SHELL_API);
         if ((managedDependency != null) && !Strings.isNullOrEmpty(managedDependency.getVersion()))
            apiVersion = managedDependency.getVersion();
      }

      if (apiVersion == null)
      {
         // Now completely give up and just use the result from the build
         Dependency effectiveDependency = deps.getEffectiveDependency(SHELL_API);
         if (effectiveDependency != null)
            apiVersion = effectiveDependency.getVersion();
         else
            apiVersion = environment.getRuntimeVersion();
      }

      /**
       * Make sure that our PROVIDED modules are not included in the module dependencies
       */
      // TODO Weld bug requires us to correct /add module for Seam Render dependency
      List<String> groupIds = Arrays.asList("org.jboss.seam.render", "org.jboss.forge");
      List<String> providedDeps = Arrays.asList("forge-javaee-api", "forge-maven-api", "forge-scaffold-api",
               "forge-scaffoldx-api", "forge-shell-api");
      List<Dependency> dependencies = deps.getDependencies();
      for (Dependency dependency : dependencies)
      {
         if (groupIds.contains(dependency.getGroupId())
                  && !(ScopeType.PROVIDED.equals(dependency.getScopeTypeEnum())
                  || ScopeType.TEST.equals(dependency.getScopeTypeEnum())))
         {
            ShellMessages.warn(shell, "Dependency [" + dependency.toCoordinates()
                     + "] was not correctly marked as PROVIDED scope; this has been corrected.");
            deps.addDirectDependency(DependencyBuilder.create(dependency).setScopeType(ScopeType.PROVIDED));
         }
         else if (dependency.getGroupId().equals("org.jboss.forge")
                  && !providedDeps.contains(dependency.getArtifactId())
                  && !ScopeType.TEST.equals(deps.getEffectiveDependency(dependency).getScopeTypeEnum()))
         {
            ShellMessages.warn(shell,
                     "Plugin has a dependency on internal Forge API [" + dependency
                              + "] - this is not allowed and may cause failures.");
         }
      }

      ShellMessages.info(shell, "Invoking build with underlying build system.");
      // Build the whole project
      // Inter-module dependencies are not resolved when mvn package is just run. Perhaps it should install ?
      Resource<?> artifact;
      if (rootProject.equals(pluginProject))
      {
         artifact = rootProject.getFacet(PackagingFacet.class).createBuilder().runTests(false).build();
      }
      else
      {
         rootProject.getFacet(PackagingFacet.class).createBuilder().addArguments("clean", "install").runTests(false).build();
         artifact = pluginProject.getFacet(PackagingFacet.class).getFinalArtifact();
      }
      // However, get only the necessary plugin artifact
      if ((artifact != null) && artifact.exists())
      {
         MetadataFacet meta = rootProject.getFacet(MetadataFacet.class);
         Dependency dep = meta.getOutputDependency();

         ShellMessages.info(shell, "Installing plugin artifact.");

         // TODO Figure out a better plugin versioning strategy than random numbers, also see if unloading is
         // possible to avoid this entirely.
         createModule(
                  pluginProject,
                  DependencyBuilder.create(dep).setVersion(
                           dep.getVersion() + "-" + UUID.randomUUID().toString()),
                  artifact, apiVersion);
      }
      else
      {
         throw new IllegalStateException("Build artifact [" + artifact
                  + "] is missing and cannot be installed. Please resolve build errors and try again.");
      }
   }

   /**
    * This method will try its best to find the plugin coordinates for a given project
    * 
    * @param project
    * @return
    */
   Project findPluginProject(final Project rootProject, final Dependency coordinates)
   {
      final Project pluginProject;
      Model rootPom = rootProject.getFacet(MavenCoreFacet.class).getPOM();
      Dependency projectDependency = DependencyBuilder.create()
               .setGroupId(rootPom.getGroupId() == null ? rootPom.getParent().getGroupId() : rootPom.getGroupId())
               .setArtifactId(rootPom.getArtifactId()).setPackagingType(rootPom.getPackaging());

      // 1) Check if the root project is a multimodule project
      if (projectDependency.getPackagingTypeEnum() == PackagingType.BASIC)
      {
         Project tempPluginProject = null;
         DirectoryResource projectDir = rootProject.getProjectRoot();
         for (String module : rootPom.getModules())
         {
            DirectoryResource moduleDir = projectDir.getChildDirectory(module);
            // 1.1) If yes, traverse the projects
            Project moduleProject = projectFactory.findProject(moduleDir);
            // If module is empty, avoid infinite recursion
            if (moduleProject.equals(rootProject))
            {
               continue;
            }
            tempPluginProject = findPluginProject(moduleProject, coordinates);
            if (tempPluginProject != null)
            {
               break;
            }
         }
         pluginProject = tempPluginProject;
      }
      else
      {
         // 1.2) If not, check if the provided coordinate (if not null) matches the requested project
         // If no coordinates were provided or if the coordinate matches the current POM, return the the root project is
         // the plugin project
         if (coordinates == null || DependencyBuilder.areEquivalent(projectDependency, coordinates))
         {
            pluginProject = rootProject;
         }
         else
         {
            pluginProject = null;
         }
      }
      return pluginProject;
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
            final String apiVersion) throws Abort
   {

      boolean dependenciesAsResourceRoot = needDependenciesAsResourceRoot(project);

      DirectoryResource moduleDir = getOrCreatePluginModuleDirectory(dep);
      String pluginName = dep.getGroupId() + "." + dep.getArtifactId();
      String pluginSlot = dep.getVersion();

      FileResource<?> moduleXml = (FileResource<?>) moduleDir.getChild("module.xml");
      if (moduleXml.exists()
               && !shell.promptBoolean(
                        "An existing installation for version [" + pluginSlot
                                 + "] of this plugin was found. Replace it?", true))
      {
         throw new Abort("Aborted.");
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
      dependencies.createChild("module").attribute("name", "org.jboss.forge.scaffoldx.api")
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
         else if (DependencyBuilder.areEquivalent(d, DependencyBuilder.create("org.jboss.forge:forge-scaffoldx-api")))
         {
            module.getSingle("dependencies").createChild("module")
                     .attribute("name", "org.jboss.forge.scaffoldx.api")
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
            ShellMessages.error(shell,
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
         ShellMessages.warn(shell, "Could not resolve dependency [" + d.toCoordinates() + "]");
      }

      return artifacts;
   }

   private void registerPlugin(final String pluginName, final String pluginSlot, final String apiVersion)
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

   private DirectoryResource getOrCreatePluginModuleDirectory(final Dependency dep)
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

   private DirectoryResource getOrCreatePluginDependenciesModuleDirectory(final Dependency dep)
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
