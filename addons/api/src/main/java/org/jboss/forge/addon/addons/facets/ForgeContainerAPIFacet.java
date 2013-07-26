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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ForgeContainerAPIFacet extends AbstractFacet<Project> implements ProjectFacet
{
   public static Dependency FORGE_CONTAINER_API_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace")
            .setArtifactId("container-cdi-api")
            .setScopeType("provided");

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      Dependency dependency = installer.install(getFaceted(), FORGE_CONTAINER_API_DEPENDENCY);
      return dependency != null;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(origin, FORGE_CONTAINER_API_DEPENDENCY);
   }
}
