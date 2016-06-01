/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.stacks;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraints;
import org.jboss.forge.furnace.util.Annotations;

/**
 * Used to inspect types that may or may not depend on {@link Facet}s or packaging types.
 * 
 * Copied from {@link FacetInspector}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class StackInspector
{
   /**
    * Inspect the given {@link Class}, and return <code>true</code> if all declared constraints have been satisfied.
    */
   public static boolean isConstraintSatisfied(Project project,
            Set<Class<ProjectFacet>> requiredFacets)
   {
      boolean constraintsSatisfied = true;
      Iterable<StackFacet> stackFacets = project.getFacets(StackFacet.class);
      OUTER: for (StackFacet stackFacet : stackFacets)
      {
         for (Class<ProjectFacet> type : requiredFacets)
         {
            if (!stackFacet.getStack().supports(type))
            {
               constraintsSatisfied = false;
               break OUTER;
            }
         }
      }
      return constraintsSatisfied;
   }

   /**
    * Inspect the given {@link Class}, and return <code>true</code> if any circular dependencies are detected between
    * any {@link FacetConstraint} declarations.
    */
   static <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean hasCircularConstraints(
            Class<?> inspectedType)
   {
      Set<Class<FACETTYPE>> allRelatedFacets = getAllRelatedFacets(inspectedType);
      for (Class<? extends Facet<?>> requirement : allRelatedFacets)
      {
         for (Class<? extends Facet<?>> subrequirement : getAllRelatedFacets(requirement))
         {
            if (subrequirement.isAssignableFrom(requirement))
               return true;
         }
      }
      return false;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getRelatedFacets(final Class<?> inspectedType)
   {
      Set<Class<FACETTYPE>> result = new LinkedHashSet<Class<FACETTYPE>>();

      if (Annotations.isAnnotationPresent(inspectedType, StackConstraints.class))
      {
         StackConstraints constraints = Annotations.getAnnotation(inspectedType, StackConstraints.class);
         for (StackConstraint constraint : constraints.value())
         {
            for (Class<? extends Facet> facetType : constraint.value())
            {
               if (Facet.class.isAssignableFrom(facetType) && !facetType.isAssignableFrom(inspectedType))
                  result.add((Class<FACETTYPE>) facetType);
            }
         }
      }

      if (Annotations.isAnnotationPresent(inspectedType, StackConstraint.class))
      {
         StackConstraint constraint = Annotations.getAnnotation(inspectedType, StackConstraint.class);
         for (Class<? extends Facet> facetType : constraint.value())
         {
            if (Facet.class.isAssignableFrom(facetType) && !facetType.isAssignableFrom(inspectedType))
               result.add((Class<FACETTYPE>) facetType);
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} for all {@link Facet} types from all {@link FacetConstraint} declarations. This
    * method inspects the entire constraint tree.
    */
   public static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getAllRelatedFacets(final Class<?> inspectedType)
   {
      Set<Class<FACETTYPE>> seen = new LinkedHashSet<Class<FACETTYPE>>();
      return getAllRelatedFacets(seen, inspectedType);
   }

   private static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getAllRelatedFacets(
            Set<Class<FACETTYPE>> seen, final Class<?> inspectedType)
   {
      Set<Class<FACETTYPE>> result = new LinkedHashSet<Class<FACETTYPE>>();
      Set<Class<FACETTYPE>> related = getRelatedFacets(inspectedType);

      for (Class<FACETTYPE> relatedType : related)
      {
         if (!seen.contains(relatedType))
         {
            seen.add(relatedType);
            result.addAll(getAllRelatedFacets(seen, relatedType));
         }
      }

      result.addAll(related);
      return result;
   }
}