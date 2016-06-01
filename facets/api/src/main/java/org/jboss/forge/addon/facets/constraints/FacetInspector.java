/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets.constraints;

import java.util.LinkedHashSet;
import java.util.Set;

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
    * Inspect the given {@link Class}, and return <code>true</code> if all declared constraints have been satisfied.
    */
   public static <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean isConstraintSatisfied(
            Faceted<FACETTYPE> faceted, Set<Class<FACETTYPE>> requiredFacets)
   {
      boolean constraintsSatisfied = true;
      for (Class<FACETTYPE> type : requiredFacets)
      {
         if (!faceted.hasFacet(type))
         {
            constraintsSatisfied = false;
            break;
         }
      }
      return constraintsSatisfied;
   }

   /**
    * Inspect the given {@link Class}, and return <code>true</code> if any circular dependencies are detected between
    * any {@link FacetConstraint} declarations.
    */
   public static <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean hasCircularConstraints(
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

   /**
    * Inspect the given {@link Class} for any {@link FacetConstraintType#OPTIONAL} dependency {@link Facet} types.
    */
   public static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getOptionalFacets(final Class<?> inspectedType)
   {
      return getRelatedFacets(inspectedType, FacetConstraintType.OPTIONAL);
   }

   /**
    * Inspect the given {@link Class} for any {@link FacetConstraintType#REQUIRED} dependency {@link Facet} types.
    */
   public static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getRequiredFacets(final Class<?> inspectedType)
   {
      return getRelatedFacets(inspectedType, FacetConstraintType.REQUIRED);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getRelatedFacets(final Class<?> inspectedType,
            FacetConstraintType... constraintTypes)
   {
      Set<Class<FACETTYPE>> result = new LinkedHashSet<Class<FACETTYPE>>();

      if (Annotations.isAnnotationPresent(inspectedType, FacetConstraints.class))
      {
         FacetConstraints constraints = Annotations.getAnnotation(inspectedType, FacetConstraints.class);
         for (FacetConstraint constraint : constraints.value())
         {
            if (constraint.value() != null
                     && (constraintTypes == null || constraintTypes.length == 0 || equalsAny(constraint.type(),
                              constraintTypes)))
            {
               for (Class<? extends Facet> facetType : constraint.value())
               {
                  if (Facet.class.isAssignableFrom(facetType) && !facetType.isAssignableFrom(inspectedType))
                     result.add((Class<FACETTYPE>) facetType);
               }
            }
         }
      }

      if (Annotations.isAnnotationPresent(inspectedType, FacetConstraint.class))
      {
         FacetConstraint constraint = Annotations.getAnnotation(inspectedType, FacetConstraint.class);
         if (constraint.value() != null
                  && (constraintTypes == null || constraintTypes.length == 0 || equalsAny(constraint.type(),
                           constraintTypes)))
         {
            for (Class<? extends Facet> facetType : constraint.value())
            {
               if (Facet.class.isAssignableFrom(facetType) && !facetType.isAssignableFrom(inspectedType))
                  result.add((Class<FACETTYPE>) facetType);
            }
         }
      }

      return result;
   }

   private static boolean equalsAny(FacetConstraintType type, FacetConstraintType... validTypes)
   {
      if (validTypes != null)
      {
         for (FacetConstraintType validType : validTypes)
         {
            if (validType.equals(type))
               return true;
         }
      }
      return false;
   }

   /**
    * Inspect the given {@link Class} for all {@link FacetConstraintType#OPTIONAL} dependency {@link Facet} types. This
    * method inspects the entire constraint tree.
    */
   public static <FACETEDTYPE extends Faceted<FACETTYPE>, FACETTYPE extends Facet<FACETEDTYPE>> Set<Class<FACETTYPE>> getAllOptionalFacets(
            final Class<FACETTYPE> inspectedType)
   {
      Set<Class<FACETTYPE>> seen = new LinkedHashSet<Class<FACETTYPE>>();
      return getAllRelatedFacets(seen, inspectedType, FacetConstraintType.OPTIONAL);
   }

   /**
    * Inspect the given {@link Class} for all {@link FacetConstraintType#REQUIRED} dependency {@link Facet} types. This
    * method inspects the entire constraint tree.
    */
   public static <FACETTYPE extends Facet<?>> Set<Class<FACETTYPE>> getAllRequiredFacets(final Class<?> inspectedType)
   {
      Set<Class<FACETTYPE>> seen = new LinkedHashSet<Class<FACETTYPE>>();
      return getAllRelatedFacets(seen, inspectedType, FacetConstraintType.REQUIRED);
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
            Set<Class<FACETTYPE>> seen, final Class<?> inspectedType, FacetConstraintType... constraintTypes)
   {
      Set<Class<FACETTYPE>> result = new LinkedHashSet<Class<FACETTYPE>>();
      Set<Class<FACETTYPE>> related = getRelatedFacets(inspectedType, constraintTypes);

      for (Class<FACETTYPE> relatedType : related)
      {
         if (!seen.contains(relatedType))
         {
            seen.add(relatedType);
            result.addAll(getAllRelatedFacets(seen, relatedType, constraintTypes));
         }
      }

      result.addAll(related);
      return result;
   }
}