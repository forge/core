/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.facets;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Configures the project as an complex forge-addon project.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ JavaCompilerFacet.class, FurnaceVersionFacet.class })
public class AddonParentFacet extends AbstractFacet<Project>implements ProjectFacet
{
   @Override
   public boolean install()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      installManagedDependency(installer, FurnaceAPIFacet.FURNACE_API_DEPENDENCY);
      installManagedDependency(installer, DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY);
      installManagedDependency(installer, DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY);
      installManagedDependency(installer, AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY);
      installManagedDependency(installer, AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY);
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      return installer.isManaged(origin, DefaultFurnaceContainerFacet.FURNACE_CONTAINER_DEPENDENCY)
               && installer.isManaged(origin, FurnaceAPIFacet.FURNACE_API_DEPENDENCY)
               && installer.isManaged(origin, DefaultFurnaceContainerAPIFacet.FURNACE_CONTAINER_API_DEPENDENCY)
               && installer.isManaged(origin, AddonTestFacet.FURNACE_TEST_ADAPTER_DEPENDENCY)
               && installer.isManaged(origin, AddonTestFacet.FURNACE_TEST_HARNESS_DEPENDENCY);
   }

   private Dependency installManagedDependency(DependencyInstaller installer, Dependency dependency)
   {
      if (!installer.isManaged(origin, dependency))
      {
         return installer.installManaged(origin, DependencyBuilder.create(dependency)
                  .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
      }
      else
      {
         return dependency;
      }
   }

}