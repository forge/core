/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.facets.constraints;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.furnace.util.Annotations;

/**
 * Used to inspect types that may or may not depend on {@link Facet}s or packaging types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class FacetInspector
{
   /**
    * Inspect the given {@link Class} for any dependencies to {@link Facet} types.
    */
   @SuppressWarnings("unchecked")
   public static <FACETEDTYPE extends Faceted<FACETTYPE, FACETEDTYPE>, FACETTYPE extends Facet<FACETEDTYPE, FACETTYPE>> List<Class<FACETTYPE>> getRequiredFacets(
            final Class<FACETTYPE> type)
   {
      List<Class<FACETTYPE>> result = new ArrayList<Class<FACETTYPE>>();

      if (Annotations.isAnnotationPresent(type, RequiresFacet.class))
      {
         RequiresFacet requires = Annotations.getAnnotation(type, RequiresFacet.class);
         if (requires.value() != null)
         {
            for (Class<? extends Facet<?, ?>> facetType : requires.value())
            {
               if (Facet.class.isAssignableFrom(facetType))
                  result.add((Class<FACETTYPE>) facetType);
            }
         }
      }

      return result;
   }

}