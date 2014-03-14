/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
 * Installs the BOM into the project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint({ FurnaceVersionFacet.class })
public class ForgeBOMFacet extends AbstractFacet<Project> implements ProjectFacet
{
   @Inject
   private DependencyInstaller installer;

   public static Dependency FORGE_BOM_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge")
            .setArtifactId("forge-bom")
            .setPackaging("pom")
            .setScopeType("import");

   @Override
   public boolean install()
   {
      Dependency dependency = installer.installManaged(getFaceted(), DependencyBuilder
               .create(FORGE_BOM_DEPENDENCY)
               .setVersion(FurnaceVersionFacet.VERSION_PROPERTY));
      return dependency != null;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isManaged(origin, FORGE_BOM_DEPENDENCY);
   }

}
