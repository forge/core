/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addon.addons.facets.AddonAPIFacet;
import org.jboss.forge.addon.addons.facets.AddonAddonFacet;
import org.jboss.forge.addon.addons.facets.AddonClassifierFacet;
import org.jboss.forge.addon.addons.facets.AddonImplFacet;
import org.jboss.forge.addon.addons.facets.AddonParentFacet;
import org.jboss.forge.addon.addons.facets.AddonSPIFacet;
import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.addons.facets.DefaultFurnaceContainerFacet;
import org.jboss.forge.addon.addons.facets.FurnacePluginFacet;
import org.jboss.forge.addon.addons.facets.FurnaceVersionFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaPackageInfo;

/**
 * Creates Furnace Addon projects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@SuppressWarnings("unchecked")
public class AddonProjectConfigurator
{
   private static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

   private final Logger log = Logger.getLogger(getClass().getName());

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private DependencyInstaller dependencyInstaller;

   @Inject
   private MavenBuildSystem buildSystem;

   public void setupSimpleAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException
   {
      generateReadme(project);
      facetFactory.install(project, FurnaceVersionFacet.class);
      project.getFacet(FurnaceVersionFacet.class).setVersion(forgeVersion.toString());

      facetFactory.install(project, FurnacePluginFacet.class);
      facetFactory.install(project, AddonClassifierFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaCompilerFacet.class);
      facetFactory.install(project, DefaultFurnaceContainerFacet.class);
      facetFactory.install(project, CDIFacet_1_1.class);
      facetFactory.install(project, AddonTestFacet.class);

      JavaSourceFacet javaSource = project.getFacet(JavaSourceFacet.class);
      javaSource.saveJavaSource(JavaParser.create(JavaPackageInfo.class).setPackage(javaSource.getBasePackage()));

      installSelectedAddons(project, dependencyAddons, false);
   }

   /**
    * Create a Furnace Project with the full structure (api,impl,tests,spi and addon)
    * 
    * @throws FacetNotFoundException
    * @throws FileNotFoundException
    */
   public void setupComplexAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException
   {
      generateReadme(project);
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      String projectName = metadata.getProjectName();
      metadata.setProjectName(projectName + "-parent");
      DirectoryResource newRoot = project.getProjectRoot().getParent().getChildDirectory(metadata.getProjectName());
      // FORGE-877: there's an eclipse (not m2e) limitation that says if a project is located directly in the workspace
      // folder, then the imported project's name is always the same as the folder it is contained in.
      if (newRoot.exists() || !project.getProjectRoot().renameTo(newRoot))
      {
         log.warning("Could not rename project root");
      }

      facetFactory.install(project, AddonParentFacet.class);
      project.getFacet(FurnaceVersionFacet.class).setVersion(forgeVersion.toString());

      Project addonProject = createSubmoduleProject(project, "addon", projectName, AddonAddonFacet.class);
      Project apiProject = createSubmoduleProject(project, "api", projectName + "-api", AddonAPIFacet.class,
               CDIFacet_1_1.class);
      Project implProject = createSubmoduleProject(project, "impl", projectName + "-impl", AddonImplFacet.class,
               CDIFacet_1_1.class);
      Project spiProject = createSubmoduleProject(project, "spi", projectName + "-spi", AddonSPIFacet.class);
      Project testsProject = createSubmoduleProject(project, "tests", projectName + "-tests", AddonTestFacet.class);

      Dependency apiProjectDependency = apiProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency implProjectDependency = implProject.getFacet(MetadataFacet.class).getOutputDependency();

      Dependency spiProjectDependency = DependencyBuilder.create(
               spiProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);

      Dependency addonProjectDependency = DependencyBuilder.create(
               addonProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);

      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(addonProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(apiProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(implProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(spiProjectDependency).setVersion("${project.version}"));

      for (Project p : Arrays.asList(addonProject, apiProject, implProject, spiProject))
      {
         JavaSourceFacet javaSource = p.getFacet(JavaSourceFacet.class);
         javaSource.saveJavaSource(JavaParser.create(JavaPackageInfo.class).setPackage(
                  project.getFacet(MetadataFacet.class).getTopLevelPackage()));
      }

      installSelectedAddons(project, dependencyAddons, true);
      installSelectedAddons(addonProject, dependencyAddons, false);
      installSelectedAddons(testsProject, dependencyAddons, false);

      dependencyInstaller.install(addonProject, DependencyBuilder.create(apiProjectDependency));
      dependencyInstaller.install(addonProject, DependencyBuilder.create(implProjectDependency)
               .setScopeType("runtime"));
      dependencyInstaller.install(addonProject, DependencyBuilder.create(spiProjectDependency));

      dependencyInstaller.install(implProject, DependencyBuilder.create(apiProjectDependency).setScopeType("provided"));
      dependencyInstaller.install(implProject, DependencyBuilder.create(spiProjectDependency).setScopeType("provided"));

      dependencyInstaller.install(apiProject, DependencyBuilder.create(spiProjectDependency).setScopeType("provided"));

      dependencyInstaller.install(testsProject, addonProjectDependency);
   }

   /**
    * @param project
    */
   private void generateReadme(Project project)
   {
      String readmeTemplate = Streams.toString(getClass().getResourceAsStream("README.asciidoc"));
      FileResource<?> child = project.getProjectRoot().getChildOfType(FileResource.class, "README.asciidoc");

      // TODO: Replace with template addon
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      readmeTemplate = readmeTemplate.replaceAll("\\{\\{ADDON_GROUP_ID\\}\\}", metadata.getTopLevelPackage());
      readmeTemplate = readmeTemplate.replaceAll("\\{\\{ADDON_ARTIFACT_ID\\}\\}", metadata.getProjectName());
      child.createNewFile();
      child.setContents(readmeTemplate);
   }

   private void installSelectedAddons(final Project project, Iterable<AddonId> addons, boolean managed)
   {
      if (addons != null)
         for (AddonId addon : addons)
         {
            String[] mavenCoords = addon.getName().split(":");
            DependencyBuilder dependency = DependencyBuilder.create().setGroupId(mavenCoords[0])
                     .setArtifactId(mavenCoords[1])
                     .setVersion(addon.getVersion().toString()).setClassifier(FORGE_ADDON_CLASSIFIER);
            if (managed)
            {
               dependencyInstaller.installManaged(project, dependency);
            }
            else
            {
               dependencyInstaller.install(project, dependency);
            }
         }
   }

   private Project createSubmoduleProject(final Project parent, String moduleName, String artifactId,
            Class<? extends ProjectFacet>... requiredProjectFacets)
   {
      DirectoryResource location = parent.getProjectRoot().getOrCreateChildDirectory(moduleName);

      List<Class<? extends ProjectFacet>> facets = new ArrayList<Class<? extends ProjectFacet>>();
      facets.add(ResourcesFacet.class);
      facets.addAll(Arrays.asList(requiredProjectFacets));
      Project project = projectFactory.createProject(location, buildSystem, facets);

      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      metadata.setProjectName(artifactId);
      return project;
   }
}
