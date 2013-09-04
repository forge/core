/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetFactoryImpl implements FacetFactory
{
   @Inject
   private AddonRegistry registry;

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE create(
            FACETEDTYPE origin, Class<FACETTYPE> type)
   {
      FACETTYPE instance = create(type);
      if (instance instanceof MutableFacet)
         ((MutableFacet<FACETEDTYPE>) instance).setFaceted(origin);
      else
         throw new IllegalArgumentException("Facet type [" + type.getName() + "] does not support setting an origin.");
      return instance;
   }

   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE create(
            Class<FACETTYPE> type)
   {
      Assert.notNull(type, "Facet type must not be null.");
      Imported<FACETTYPE> instance = registry.getServices(type);

      if (instance.isAmbiguous())
      {
         if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers()))
         {
            FACETTYPE facet = instance.selectExact(type);
            return facet;
         }

         throw new FacetIsAmbiguousException("Cannot resolve facet type [" + type.getName()
                  + "] because multiple ambiguous types were found: " + instance);
      }
      else if (!instance.isSatisfied())
         throw new FacetNotFoundException("Could not find Facet of type [" + type.getName() + "]");
      else
         return instance.get();
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> Iterable<FACETTYPE> createFacets(
            FACETEDTYPE origin, Class<FACETTYPE> type)
   {
      Iterable<FACETTYPE> facets = createFacets(type);
      for (FACETTYPE facet : facets)
      {
         if (facet instanceof MutableFacet)
            ((MutableFacet<FACETEDTYPE>) facet).setFaceted(origin);
         else
            throw new IllegalArgumentException("Facet type [" + type.getName()
                     + "] does not support setting an origin.");
      }
      return facets;
   }

   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> Iterable<FACETTYPE> createFacets(
            Class<FACETTYPE> type)
   {
      Assert.notNull(type, "Facet type must not be null.");
      Imported<FACETTYPE> instances = registry.getServices(type);
      Set<FACETTYPE> facets = new HashSet<FACETTYPE>();
      for (FACETTYPE instance : instances)
      {
         facets.add(instance);
      }
      return facets;
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE install(
            FACETEDTYPE origin, Class<FACETTYPE> type)
            throws FacetNotFoundException
   {
      FACETTYPE facet = create(origin, type);
      if (!install(origin, facet))
      {
         throw new IllegalStateException("Facet type [" + type.getName()
                  + "] could not be installed into [" + origin + "] of type [" + origin.getClass().getName()
                  + "]. You may wish to check for inconsistent origin state as partial installation may have occurred.");
      }
      return facet;
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(
            FACETEDTYPE origin, FACETTYPE facet)
   {
      Assert.notNull(origin, "Origin instance must not be null.");
      Assert.notNull(facet, "Facet instance must not be null.");

      Set<Class<FACETTYPE>> seen = new LinkedHashSet<Class<FACETTYPE>>();
      return install(seen, origin, facet);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(
            Set<Class<FACETTYPE>> seen, FACETEDTYPE origin, FACETTYPE facet)
   {
      if (FacetInspector.hasCircularConstraints(facet.getClass()))
         throw new IllegalStateException("Circular dependencies detected in @" + RequiresFacet.class.getSimpleName()
                  + " annotation located at [" + facet.getClass().getName() + "]");

      seen.add((Class<FACETTYPE>) facet.getClass());
      Faceted<FACETTYPE> faceted = (Faceted<FACETTYPE>) origin;
      Assert.isTrue(faceted instanceof MutableFaceted, "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      if (facet.getFaceted() == null && facet instanceof MutableFacet)
      {
         ((MutableFacet<FACETEDTYPE>) facet).setFaceted(origin);
      }

      Assert.isTrue(origin.equals(facet.getFaceted()), "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      Set<Class<FACETTYPE>> requiredFacets = FacetInspector.getRequiredFacets(facet.getClass());

      List<Class<FACETTYPE>> facetsToInstall = new ArrayList<Class<FACETTYPE>>();
      for (Class<FACETTYPE> requirementType : requiredFacets)
      {
         boolean isSeen = false;
         for (Class<FACETTYPE> seenType : seen)
         {
            if (requirementType.isAssignableFrom(seenType))
            {
               isSeen = true;
               break;
            }
         }

         if (!isSeen && !origin.hasFacet((Class) requirementType))
         {
            facetsToInstall.add(requirementType);
         }
      }

      for (Class<FACETTYPE> requirementType : facetsToInstall)
      {
         FACETTYPE requirement = create(origin, requirementType);
         install(seen, origin, requirement);
      }

      boolean result = false;
      if (faceted.hasFacet((Class<? extends FACETTYPE>) facet.getClass()))
         result = true;
      else if (FacetInspector.isConstraintSatisfied(faceted, requiredFacets))
         result = ((MutableFaceted<FACETTYPE>) faceted).install(facet);
      return result;
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE register(
            FACETEDTYPE origin, Class<FACETTYPE> type) throws FacetNotFoundException
   {
      FACETTYPE facet = create(origin, type);
      if (!register(origin, facet))
      {
         throw new IllegalStateException("Facet type [" + type.getName()
                  + "] could not be registered into [" + origin
                  + "] of type [" + origin.getClass().getName()
                  + "].");
      }
      return facet;
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean register(
            FACETEDTYPE origin, FACETTYPE facet) throws IllegalArgumentException
   {
      Assert.notNull(origin, "Origin instance must not be null.");
      Assert.notNull(facet, "Facet instance must not be null.");

      Set<Class<FACETTYPE>> seen = new LinkedHashSet<Class<FACETTYPE>>();
      return register(seen, origin, facet);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean register(
            Set<Class<FACETTYPE>> seen, FACETEDTYPE origin, FACETTYPE facet)
   {
      if (FacetInspector.hasCircularConstraints(facet.getClass()))
         throw new IllegalStateException("Circular dependencies detected in @" + RequiresFacet.class.getSimpleName()
                  + " annotation located at [" + facet.getClass().getName() + "]");

      seen.add((Class<FACETTYPE>) facet.getClass());
      Faceted<FACETTYPE> faceted = (Faceted<FACETTYPE>) origin;
      Assert.isTrue(faceted instanceof MutableFaceted, "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      if (facet.getFaceted() == null && facet instanceof MutableFacet)
      {
         ((MutableFacet<FACETEDTYPE>) facet).setFaceted(origin);
      }

      Assert.isTrue(origin.equals(facet.getFaceted()), "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      Set<Class<FACETTYPE>> requiredFacets = FacetInspector.getRequiredFacets(facet.getClass());

      List<Class<FACETTYPE>> facetsToInstall = new ArrayList<Class<FACETTYPE>>();
      for (Class<FACETTYPE> requirementType : requiredFacets)
      {
         boolean isSeen = false;
         for (Class<FACETTYPE> seenType : seen)
         {
            if (requirementType.isAssignableFrom(seenType))
            {
               isSeen = true;
               break;
            }
         }

         if (!isSeen && !origin.hasFacet((Class) requirementType))
         {
            facetsToInstall.add(requirementType);
         }
      }

      for (Class<FACETTYPE> requirementType : facetsToInstall)
      {
         FACETTYPE requirement = create(origin, requirementType);
         register(seen, origin, requirement);
      }

      boolean result = false;
      if (faceted.hasFacet((Class<? extends FACETTYPE>) facet.getClass()))
         result = true;
      else if (FacetInspector.isConstraintSatisfied(faceted, requiredFacets))
         result = ((MutableFaceted<FACETTYPE>) faceted).register(facet);
      return result;
   }

}
