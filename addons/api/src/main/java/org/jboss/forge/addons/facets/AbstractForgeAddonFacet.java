/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons.facets;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * Abstract class for all Forge facets
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public abstract class AbstractForgeAddonFacet extends AbstractFacet<Project> implements ProjectFacet
{
   @Inject
   private FacetFactory facetFactory;

   /**
    * Should return a list of the required facets for this facet to work
    */
   protected abstract List<Class<? extends ProjectFacet>> getRequiredFacets();

   @Override
   public boolean install()
   {
      for (Class<? extends ProjectFacet> facet : getRequiredFacets())
      {
         facetFactory.install(facet, getOrigin());
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getOrigin().hasAllFacets(getRequiredFacets());
   }
}
