/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetInspector;
import org.jboss.forge.addon.facets.events.FacetEvent;
import org.jboss.forge.addon.facets.events.FacetInstalledImpl;
import org.jboss.forge.addon.facets.events.FacetRegisteredImpl;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.forge.furnace.util.Sets;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetFactoryImpl implements FacetFactory
{
   private static final Logger log = Logger.getLogger(FacetFactoryImpl.class.getName());

   private AddonRegistry registry;

   private final Set<FacetListener> listeners = Sets.getConcurrentSet(FacetListener.class);

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
      Imported<FACETTYPE> instance = getAddonRegistry().getServices(type);

      if (instance.isAmbiguous())
      {
         if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers()))
         {
            FACETTYPE facet = instance.selectExact(type);
            return facet;
         }

         throw new FacetIsAmbiguousException("Cannot resolve ambiguous facet type [" + type.getName()
                  + "] because multiple matching types were found: \n" + instance);
      }
      else if (instance.isUnsatisfied())
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
      Imported<FACETTYPE> instances = getAddonRegistry().getServices(type);
      Set<FACETTYPE> facets = new HashSet<>();
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
      return install(origin, type, null);
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE install(FACETEDTYPE origin,
            Class<FACETTYPE> type, Predicate<FACETTYPE> filter) throws FacetNotFoundException,
            IllegalStateException, FacetIsAmbiguousException
   {
      FACETTYPE facet = create(origin, type);
      if (!install(origin, facet, filter))
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
      return install(origin, facet, null);
   }

   @Override
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(FACETEDTYPE origin,
            FACETTYPE facet, Predicate<FACETTYPE> filter) throws IllegalArgumentException, IllegalStateException
   {
      Assert.notNull(origin, "Origin instance must not be null.");
      Assert.notNull(facet, "Facet instance must not be null.");

      Set<Class<FACETTYPE>> seen = new LinkedHashSet<>();
      return install(seen, origin, facet, filter);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(
            Set<Class<FACETTYPE>> seen, FACETEDTYPE origin, FACETTYPE facet, Predicate<FACETTYPE> filter)
   {
      if (filter == null)
         filter = new Predicate<FACETTYPE>()
         {
            @Override
            public boolean accept(FACETTYPE type)
            {
               return true;
            }
         };

      if (FacetInspector.hasCircularConstraints(facet.getClass()))
         throw new IllegalStateException("Circular dependencies detected in @" + FacetConstraint.class.getSimpleName()
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

      /*
       * Always try to register everything before installing anything.
       */
      register(origin, facet);

      Set<Class<FACETTYPE>> requiredFacets = FacetInspector.getRequiredFacets(facet.getClass());
      List<Class<FACETTYPE>> facetsToInstall = new ArrayList<>();
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
         install(seen, origin, requirement, filter);
      }

      boolean result = false;
      if (faceted.hasFacet((Class<? extends FACETTYPE>) facet.getClass()))
         result = true;
      else if (FacetInspector.isConstraintSatisfied(faceted, requiredFacets) && filter.accept(facet))
         try
         {
            result = ((MutableFaceted<FACETTYPE>) faceted).install(facet);
            // Firing Facet installed event
            if (result)
            {
               fireFacetEvent(new FacetInstalledImpl(facet));
            }
         }
         catch (Exception e)
         {
            log.log(Level.WARNING, "Could not install Facet of type [" + facet.getClass() + "], due to exception: ", e);
            result = false;
         }
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

      Set<Class<FACETTYPE>> seen = new LinkedHashSet<>();
      return register(seen, origin, facet);
   }

   @Override
   public ListenerRegistration<FacetListener> addFacetListener(final FacetListener listener)
   {
      listeners.add(listener);
      return new ListenerRegistration<FacetListener>()
      {
         @Override
         public FacetListener removeListener()
         {
            listeners.remove(listener);
            return listener;
         }
      };
   }

   private void fireFacetEvent(FacetEvent event)
   {
      for (FacetListener listener : getFacetListeners())
      {
         try
         {
            listener.processEvent(event);
         }
         catch (Throwable e)
         {
            log.log(Level.WARNING, "Error while firing " + event, e);
         }
      }
      // Fire event using Furnace's event architecture
      getAddonRegistry().getEventManager().fireEvent(event);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean register(
            Set<Class<FACETTYPE>> seen, FACETEDTYPE origin, FACETTYPE facet)
   {
      Class<? extends Facet> facetClass = facet.getClass();
      if (FacetInspector.hasCircularConstraints(facetClass))
         throw new IllegalStateException("Circular dependencies detected in @" + FacetConstraint.class.getSimpleName()
                  + " annotation located at [" + facetClass.getName() + "]");

      seen.add((Class<FACETTYPE>) facetClass);
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

      final Set<Class<FACETTYPE>> relatedFacets = FacetInspector.getAllRelatedFacets(facetClass);
      final Set<Class<FACETTYPE>> requiredFacets = FacetInspector.getAllRequiredFacets(facetClass);

      final List<Class<FACETTYPE>> facetsToRegister = new ArrayList<>();
      for (Class<FACETTYPE> relatedType : relatedFacets)
      {
         boolean isSeen = false;
         for (Class<FACETTYPE> seenType : seen)
         {
            if (relatedType.isAssignableFrom(seenType))
            {
               isSeen = true;
               break;
            }
         }

         if (!isSeen && !origin.hasFacet((Class) relatedType))
         {
            facetsToRegister.add(relatedType);
         }
      }

      for (Class<FACETTYPE> relatedType : facetsToRegister)
      {
         Iterable<FACETTYPE> requirements = createFacets(origin, relatedType);
         for (FACETTYPE requirement : requirements)
         {
            register(seen, origin, requirement);
         }
      }

      boolean result = false;
      if (faceted.hasFacet((Class<? extends FACETTYPE>) facetClass))
      {
         result = true;
      }
      else if (FacetInspector.isConstraintSatisfied(faceted, requiredFacets))
      {
         try
         {
            result = ((MutableFaceted<FACETTYPE>) faceted).register(facet);
            if (result)
            {
               fireFacetEvent(new FacetRegisteredImpl(facet));
            }
         }
         catch (Exception e)
         {
            log.log(Level.WARNING, "Could not register Facet of type [" + facetClass + "], due to exception: ", e);
            result = false;
         }
      }
      return result;
   }

   private AddonRegistry getAddonRegistry()
   {
      if (registry == null)
      {
         Furnace furnace = SimpleContainer.getFurnace(this.getClass().getClassLoader());
         this.registry = furnace.getAddonRegistry();
      }
      return registry;
   }

   private Set<FacetListener> getFacetListeners()
   {
      Set<FacetListener> set = new HashSet<>();
      for (FacetListener service : getAddonRegistry().getServices(FacetListener.class))
      {
         set.add(service);
      }
      set.addAll(listeners);
      return set;
   }
}
