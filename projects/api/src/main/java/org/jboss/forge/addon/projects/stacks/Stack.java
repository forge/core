/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * A {@link Stack} represents a set of technologies (represented as a {@link Set} of {@link Facet}), supported in a
 * given project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface Stack
{
   /**
    * @return The name of this stack
    */
   String getName();

   /**
    * If this stack supports the installation of this facet.
    * 
    * If the facet is a superclass of a facet supported from this Stack, it will return <code>true</code>.
    * 
    * Example:
    * <p/>
    * <code>supports(JPAFacet.class) == true</code> if this Stack is the JavaEE 6 stack
    * <code>supports(JPAFacet2_1.class) == false</code> if this Stack is the JavaEE 6 stack
    * 
    * @param facet
    * @return
    */
   default boolean supports(Class<? extends ProjectFacet> facet)
   {
      Set<Class<? extends ProjectFacet>> excludedFacets = getExcludedFacets();
      return getIncludedFacets()
               .stream()
               .anyMatch((includedFacet) -> facet.isAssignableFrom(includedFacet)
                        && !excludedFacets.contains(includedFacet));
   }

   /**
    * Example:
    * <p/>
    * <code>matches(JPAFacet2_0.class) == true</code> if this Stack is the JavaEE 6 stack
    */
   default boolean matches(Class<? extends ProjectFacet> facet)
   {
      return getIncludedFacets().contains(facet) && !getExcludedFacets().contains(facet);
   }

   /**
    * Example:
    * <p/>
    * <code>filter(JPAFacet.class, ...) == JPAFacet2_0 instance</code> if this Stack is the JavaEE 6 stack
    */
   default <T extends ProjectFacet> Set<T> filter(Class<T> type, Iterable<T> facets)
   {
      Set<Class<? extends ProjectFacet>> excludedFacets = getExcludedFacets();
      Set<Class<? extends ProjectFacet>> sameTypeFacets = getIncludedFacets().stream()
               .filter((f) -> type.isAssignableFrom(f) && !excludedFacets.contains(f))
               .collect(Collectors.toSet());
      Set<T> result = new LinkedHashSet<>();
      for (T facet : facets)
      {
         for (Class<? extends ProjectFacet> thisStackFacet : sameTypeFacets)
         {
            if (thisStackFacet.isInstance(facet))
            {
               result.add(facet);
               break;
            }
         }
      }
      return result;
   }

   /**
    * This method is supposed to be used by the auxiliary methods.
    * 
    * @return the facets included in this stack. Never <code>null</code>.
    */
   Set<Class<? extends ProjectFacet>> getIncludedFacets();

   /**
    * This method is supposed to be used by the auxiliary methods.
    * 
    * @return the facets excluded in this stack. Never <code>null</code>.
    */
   Set<Class<? extends ProjectFacet>> getExcludedFacets();
}
