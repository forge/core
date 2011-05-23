/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.project.facets.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.ForgeEvent;
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
@ForgeEvent
public class RemoveFacets
{
   private final List<Class<? extends Facet>> facetTypes;
   private boolean prompt = false;

   /**
    * Install the given facet without prompting for confirmation
    */
   public RemoveFacets(final Class<? extends Facet> facetType)
   {
      Assert.notNull(facetType, "Facet type may not be null.");
      this.facetTypes = new ArrayList<Class<? extends Facet>>();
      facetTypes.add(facetType);
   }

   /**
    * Install the given facet but first prompt for confirmation. If the user aborts, a {@link FacetActionAborted}
    * will be thrown from the statement where this event was fired.
    */
   public RemoveFacets(final boolean prompt, final Class<? extends Facet> facetType)
   {
      this(facetType);
      this.prompt = prompt;
   }

   /**
    * Install the given facets without prompting for confirmation
    */
   public RemoveFacets(final Class<? extends Facet>... facetTypes)
   {
      // FIXME This method causes warnings when used as intended... fix?
      Assert.notNull(facetTypes, "Facet types may not be null.");
      this.facetTypes = Arrays.asList(facetTypes);
   }

   /**
    * Install the given facets but first prompt for confirmation. If the user aborts, a {@link FacetActionAborted}
    * will be thrown.
    */
   public RemoveFacets(final boolean prompt, final Class<? extends Facet>... facetTypes)
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
