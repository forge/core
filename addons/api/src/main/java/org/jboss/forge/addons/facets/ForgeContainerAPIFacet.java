/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons.facets;

import javax.inject.Inject;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.dependencies.DependencyInstaller;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ForgeContainerAPIFacet extends AbstractFacet<Project> implements ProjectFacet
{
   public static Dependency FORGE_API_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.jboss.forge.furnace")
            .setArtifactId("furnace-api")
            .setScopeType("provided");

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installer.install(getOrigin(), FORGE_API_DEPENDENCY);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(origin, FORGE_API_DEPENDENCY);
   }
}
