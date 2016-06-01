/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.facet;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(DependencyFacet.class)
public class GitAPIFacetImpl extends AbstractFacet<Project> implements GitAPIFacet
{
   private static final Dependency GIT_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.eclipse.jgit")
            .setArtifactId("org.eclipse.jgit.pgm");

   public DependencyInstaller installer;

   @Override
   public boolean install()
   {
      getDependencyInstaller().install(getFaceted(), GIT_DEPENDENCY);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getDependencyInstaller().isInstalled(getFaceted(), GIT_DEPENDENCY);
   }

   private DependencyInstaller getDependencyInstaller()
   {
      if (installer == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         installer = addonRegistry.getServices(DependencyInstaller.class).get();
      }
      return installer;
   }

}