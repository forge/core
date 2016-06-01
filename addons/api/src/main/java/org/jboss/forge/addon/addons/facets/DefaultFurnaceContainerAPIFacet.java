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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Ensures that a project depends on the default Furnace container API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ FurnaceVersionFacet.class })
public class DefaultFurnaceContainerAPIFacet extends AbstractFacet<Project>implements ProjectFacet
{
   public static Dependency FURNACE_CONTAINER_API_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace.container")
            .setArtifactId("cdi-api")
            .setScopeType("provided");

   @Override
   public boolean install()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      Dependency dependency = installer.install(getFaceted(), DependencyBuilder
               .create(FURNACE_CONTAINER_API_DEPENDENCY)
               .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
      return dependency != null;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      return installer.isInstalled(origin, FURNACE_CONTAINER_API_DEPENDENCY);
   }
}
