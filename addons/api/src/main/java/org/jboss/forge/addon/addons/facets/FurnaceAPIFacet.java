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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * Ensures that a project depends on the default Furnace container API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ FurnaceVersionFacet.class })
public class FurnaceAPIFacet extends AbstractFacet<Project> implements ProjectFacet
{
   public static Dependency FURNACE_API_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace")
            .setArtifactId("furnace-api")
            .setScopeType("provided");

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      Dependency dependency = installer.install(getFaceted(), DependencyBuilder.create(FURNACE_API_DEPENDENCY)
               .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
      return dependency != null;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(origin, FURNACE_API_DEPENDENCY);
   }
}
