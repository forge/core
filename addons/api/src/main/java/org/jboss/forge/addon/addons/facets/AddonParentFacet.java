/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.facets;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * Configures the project as an complex forge-addon project.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ JavaCompilerFacet.class, FurnaceVersionFacet.class })
public class AddonParentFacet extends AbstractFacet<Project> implements ProjectFacet
{
   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installManagedDependency(FurnaceAPIFacet.FURNACE_API_DEPENDENCY);
      installManagedDependency(DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY);
      installManagedDependency(DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY);
      installManagedDependency(AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY);
      installManagedDependency(AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY);
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isManaged(origin, DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY)
               && installer.isManaged(origin, FurnaceAPIFacet.FURNACE_API_DEPENDENCY)
               && installer.isManaged(origin, DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY)
               && installer.isManaged(origin, AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY)
               && installer.isManaged(origin, AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY);
   }

   private Dependency installManagedDependency(Dependency dependency)
   {
      return installer.installManaged(origin, DependencyBuilder.create(dependency)
               .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
   }

}