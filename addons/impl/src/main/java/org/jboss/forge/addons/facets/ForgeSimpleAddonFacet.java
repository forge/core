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
import org.jboss.forge.projects.facets.DependencyFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ForgeSimpleAddonFacet extends AbstractFacet<Project> implements ProjectFacet
{
   private Dependency forgeApi = DependencyBuilder.create().setArtifactId("forge-addon-container-api")
            .setGroupId("org.jboss.forge").setScopeType("provided");

   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installer.install(getOrigin(), forgeApi);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyFacet dependencies = origin.getFacet(DependencyFacet.class);
      return dependencies != null && dependencies.hasEffectiveDependency(forgeApi);
   }
}
