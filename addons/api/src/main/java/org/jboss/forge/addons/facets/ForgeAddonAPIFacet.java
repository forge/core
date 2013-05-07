/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons.facets;

import javax.inject.Inject;

import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.dependencies.DependencyInstaller;

/**
 * Configures the project as an Addon API project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ForgeAddonAPIFacet extends AbstractFacet<Project> implements ProjectFacet
{
   @Inject
   private DependencyInstaller installer;

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return false;
   }
}