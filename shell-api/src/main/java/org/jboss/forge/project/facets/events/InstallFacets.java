/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.facets.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.FacetActionAborted;

/**
 * Event to instruct Forge to install the given {@link Facet} into the current {@link Project}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstallFacets
{
   private final List<Class<? extends Facet>> facetTypes;
   private boolean prompt = false;

   /**
    * Install the given facet without prompting for confirmation
    */
   public InstallFacets(final Class<? extends Facet> facetType)
   {
      Assert.notNull(facetType, "Facet type may not be null.");
      this.facetTypes = new ArrayList<Class<? extends Facet>>();
      facetTypes.add(facetType);
   }

   /**
    * Install the given facet but first prompt for confirmation. If the user aborts, a {@link FacetActionAborted} will
    * be thrown from the statement where this event was fired.
    */
   public InstallFacets(final boolean prompt, final Class<? extends Facet> facetType)
   {
      this(facetType);
      this.prompt = prompt;
   }

   /**
    * Install the given facets without prompting for confirmation
    */
   public InstallFacets(final Class<? extends Facet>... facetTypes)
   {
      // FIXME This method causes warnings when used as intended... fix?
      Assert.notNull(facetTypes, "Facet types may not be null.");
      this.facetTypes = Arrays.asList(facetTypes);
   }

   /**
    * Install the given facets but first prompt for confirmation. If the user aborts, a {@link FacetActionAborted} will
    * be thrown.
    */
   public InstallFacets(final boolean prompt, final Class<? extends Facet>... facetTypes)
   {
      this(facetTypes);
      this.prompt = prompt;
   }

   /**
    * Get the facet types to be installed
    */
   public List<Class<? extends Facet>> getFacetTypes()
   {
      return facetTypes;
   }

   /**
    * Return whether or not the caller has requested to prompt the user for confirmation
    */
   public boolean promptRequested()
   {
      return prompt;
   }
}
