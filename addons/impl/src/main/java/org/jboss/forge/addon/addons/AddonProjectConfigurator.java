/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addon.addons.facets.ForgeAddonAPIFacet;
import org.jboss.forge.addon.addons.facets.ForgeAddonFacet;
import org.jboss.forge.addon.addons.facets.ForgeAddonImplFacet;
import org.jboss.forge.addon.addons.facets.ForgeAddonSPIFacet;
import org.jboss.forge.addon.addons.facets.ForgeAddonTestFacet;
import org.jboss.forge.addon.addons.facets.ForgeContainerAPIFacet;
import org.jboss.forge.addon.addons.facets.ForgeContainerAddonFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.CDIFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.versions.Version;

/**
 * Creates Furnace Addon projects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Exported
@SuppressWarnings("unchecked")
public class AddonProjectConfigurator
{
   private static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

   private Logger log = Logger.getLogger(getClass().getName());

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private DependencyInstaller dependencyInstaller;

   public void setupSimpleAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
   {
      // TODO Use or remove forgeVersion parameter
      facetFactory.install(project, ForgeContainerAddonFacet.class);
      facetFactory.install(project, CDIFacet.class);
      facetFactory.install(project, ForgeAddonFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);
      installSelectedAddons(project, dependencyAddons, false);
   }

   /**
    * Create a Furnace Project with the full structure (api,impl,tests,spi and addon)
    */
   public void setupAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
   {
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

      facetFactory.install(project, JavaSourceFacet.class);
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(ForgeContainerAPIFacet.FORGE_CONTAINER_API_DEPENDENCY)
                        .setVersion(forgeVersion.toString()));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(ForgeContainerAddonFacet.FORGE_CONTAINER_DEPENDENCY)
                        .setVersion(forgeVersion.toString()));

      Project addonProject = createSubmoduleProject(project, "addon", projectName, ForgeAddonFacet.class);
      Project apiProject = createSubmoduleProject(project, "api", projectName + "-api", ForgeAddonAPIFacet.class,
               JavaSourceFacet.class, CDIFacet.class);
      Project implProject = createSubmoduleProject(project, "impl", projectName + "-impl", ForgeAddonImplFacet.class,
               JavaSourceFacet.class, CDIFacet.class);
      Project spiProject = createSubmoduleProject(project, "spi", projectName + "-spi", ForgeAddonSPIFacet.class,
               JavaSourceFacet.class, CDIFacet.class);
      Project testsProject = createSubmoduleProject(project, "tests", projectName + "-tests",
               ForgeAddonTestFacet.class, JavaSourceFacet.class);

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

      project.getProjectRoot().getChild("src").delete(true);
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

      Set<Class<? extends ProjectFacet>> facets = new LinkedHashSet<Class<? extends ProjectFacet>>();
      facets.add(ForgeContainerAPIFacet.class);
      facets.addAll(Arrays.asList(requiredProjectFacets));

      Project project = projectFactory.createProject(location, facets);

      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      metadata.setProjectName(artifactId);
      return project;
   }
}
