/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addons.facets.ForgeAddonAPIFacet;
import org.jboss.forge.addons.facets.ForgeAddonFacet;
import org.jboss.forge.addons.facets.ForgeAddonImplFacet;
import org.jboss.forge.addons.facets.ForgeAddonSPIFacet;
import org.jboss.forge.addons.facets.ForgeAddonTestFacet;
import org.jboss.forge.addons.facets.ForgeSimpleAddonFacet;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.versions.Version;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.MetadataFacet;
import org.jboss.forge.resource.DirectoryResource;

/**
 * Creates Forge Addon projects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings("unchecked")
class AddonProjectFactory
{

   private Logger log = Logger.getLogger(getClass().getName());

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private DependencyInstaller dependencyInstaller;

   public Project createSimpleAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
   {
      configureAddonProject(project);
      installSelectedAddons(project, dependencyAddons, false);
      return project;
   }

   /**
    * Create a Forge Project with the full structure (api,impl,tests,spi and addon)
    *
    * @param project
    * @param forgeVersion
    * @param dependencyAddons
    * @return the project root
    */
   public Project createAddonProject(Project project, Version forgeVersion, Iterable<AddonId> dependencyAddons)
   {
      // Project is the parent project
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      // TODO: Verify nomenclature
      String projectName = metadata.getProjectName();
      metadata.setProjectName(projectName + "-parent");
      DirectoryResource newRoot = project.getProjectRoot().getParent().getChildDirectory(metadata.getProjectName());
      // FORGE-877: there's an eclipse (not m2e) limitation that says if a project is located directly in the workspace
      // folder, then the project name must match the project folder name
      if (!project.getProjectRoot().renameTo(newRoot))
      {
         log.warning("Could not rename project root. Erasing " + newRoot);
         newRoot.delete(true);
      }
      installSelectedAddons(project, dependencyAddons, true);

      // Create ADDON Project
      createSubmoduleProject(project, "addon", projectName, ForgeAddonFacet.class);
      // Create API Project
      createSubmoduleProject(project, "api", projectName + "-api", ForgeAddonAPIFacet.class);
      // Create IMPL Project
      createSubmoduleProject(project, "impl", projectName + "-impl", ForgeAddonImplFacet.class);
      // Create SPI Project
      createSubmoduleProject(project, "spi", projectName + "-spi", ForgeAddonSPIFacet.class);
      // Create TESTS Project
      createSubmoduleProject(project, "tests", projectName + "-tests", ForgeAddonTestFacet.class);

      project.getProjectRoot().getChild("src").delete(true);
      return project;
   }

   /**
    * Configure addon
    *
    * @param project
    * @return
    */
   private void configureAddonProject(final Project project)
   {
      project.install(facetFactory.create(ForgeAddonFacet.class, project));
   }

   private void installSelectedAddons(final Project project, Iterable<AddonId> addons, boolean managed)
   {
      for (AddonId addon : addons)
      {
         String[] mavenCoords = addon.getName().split(":");
         DependencyBuilder dependency = DependencyBuilder.create().setGroupId(mavenCoords[0])
                  .setArtifactId(mavenCoords[1])
                  .setVersion(addon.getVersion().getVersionString()).setClassifier("forge-addon");
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

      Set<Class<? extends ProjectFacet>> facets = new HashSet<Class<? extends ProjectFacet>>();
      facets.addAll(Arrays.asList(requiredProjectFacets));
      facets.add(ForgeSimpleAddonFacet.class);

      Project project = projectFactory.createProject(location, facets);

      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      metadata.setProjectName(artifactId);
      return project;
   }
}
