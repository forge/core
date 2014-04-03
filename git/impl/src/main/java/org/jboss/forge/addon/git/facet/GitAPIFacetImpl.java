/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.facet;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.git.facet.GitAPIFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class GitAPIFacetImpl extends AbstractFacet<Project> implements GitAPIFacet
{
   private static final Dependency GIT_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.eclipse.jgit")
            .setArtifactId("org.eclipse.jgit.pgm");

   @Inject
   public DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installer.install(getFaceted(), GIT_DEPENDENCY);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(getFaceted(), GIT_DEPENDENCY);
   }

}