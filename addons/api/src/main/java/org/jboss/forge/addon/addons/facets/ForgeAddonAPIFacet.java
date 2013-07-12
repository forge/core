/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.facets;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * Configures the project as an Addon API project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@RequiresFacet({ ForgeContainerAPIFacet.class })
public class ForgeAddonAPIFacet extends AbstractFacet<Project> implements ProjectFacet
{
   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean isInstalled()
   {
      return getFaceted().hasAllFacets(ForgeContainerAddonFacet.class);
   }

}