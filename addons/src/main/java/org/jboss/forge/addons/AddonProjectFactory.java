/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.addons.facets.ForgeAddonAPIFacet;
import org.jboss.forge.addons.facets.ForgeAddonFacet;
import org.jboss.forge.addons.facets.ForgeAddonImplFacet;
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
import org.jboss.forge.projects.facets.PackagingFacet;
import org.jboss.forge.resource.DirectoryResource;

/**
 * Creates Forge Addon projects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings("unchecked")
public class AddonProjectFactory
{
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
      DirectoryResource projectRoot = project.getProjectRoot();
      project.getFacet(PackagingFacet.class).setPackagingType("pom");
      project.getProjectRoot().getChild("src").delete(true);
      installSelectedAddons(project, dependencyAddons, true);

      // Create ADDON Project
      createAddonProject(projectRoot);
      // Create API Project
      createAPIProject(projectRoot);
      // Create IMPL Project
      createImplProject(projectRoot);
      // Create SPI Project
      createSPIProject(projectRoot);
      // Create TESTS Project
      createTestsProject(projectRoot);
      return project;
   }

   /**
    * Configure addon
    *
    * @param project
    * @return
    */
   private void configureAddonProject(Project project)
   {
      project.install(facetFactory.create(ForgeAddonFacet.class, project));
   }

   private void installSelectedAddons(Project project, Iterable<AddonId> addons, boolean managed)
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

   private Project createAddonProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("addon");
      Project project = projectFactory.createProject(location);
      configureAddonProject(project);
      return project;
   }

   private Project createAPIProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("api");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonAPIFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   private Project createImplProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("impl");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonImplFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   private Project createSPIProject(final DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("spi");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonAPIFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

   private Project createTestsProject(DirectoryResource projectRoot)
   {
      DirectoryResource location = projectRoot.getOrCreateChildDirectory("tests");
      Project project = projectFactory.createProject(location,
               Arrays.<Class<? extends ProjectFacet>> asList(ForgeAddonTestFacet.class, ForgeSimpleAddonFacet.class));
      return project;
   }

}
