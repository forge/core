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
 * Installs the Furnace BOM into the project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ FurnaceVersionFacet.class })
public class FurnaceBOMFacet extends AbstractFacet<Project>implements ProjectFacet
{
   public static Dependency FURNACE_BOM_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace")
            .setArtifactId("furnace-bom")
            .setPackaging("pom")
            .setScopeType("import");

   @Override
   public boolean install()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      Dependency dependency = installer.installManaged(getFaceted(), DependencyBuilder
               .create(FURNACE_BOM_DEPENDENCY)
               .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
      return dependency != null;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyInstaller installer = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
      return installer.isManaged(origin, FURNACE_BOM_DEPENDENCY);
   }

}
