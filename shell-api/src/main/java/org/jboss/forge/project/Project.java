/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project;

import java.io.File;
import java.util.Collection;

import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.resources.DirectoryResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Project
{
   /**
    * Get an value from this project's internal attributes. If the value is not set, return <code>null</code> instead.
    * 
    * @param key the attribute name
    */
   public Object getAttribute(String key);

   /**
    * Set a value in this project's internal attributes.
    */
   public void setAttribute(String key, Object value);

   /**
    * Remove a value from this project's internal attributes.
    */
   public void removeAttribute(String key);

   /**
    * Return true if this project has a facet of the given type; return false otherwise.
    */
   public boolean hasFacet(Class<? extends Facet> type);

   /**
    * Return true if this project has all {@link Facet}s of the given types; otherwise, if any of the given facet types
    * is missing, return false.
    */
   public boolean hasAllFacets(Collection<Class<? extends Facet>> facetDependencies);

   /**
    * Return true if this project has all {@link Facet}s of the given types; otherwise, if any of the given facet types
    * is missing, return false.
    */
   public boolean hasAllFacets(Class<? extends Facet>... facetDependencies);

   /**
    * Return the instance of the requested {@link Facet} type, or throw a {@link FacetNotFoundException} if no
    * {@link Facet} of that type is registered.
    */
   public <F extends Facet> F getFacet(Class<F> type) throws FacetNotFoundException;

   /**
    * Return a {@link Collection} of the currently installed {@link Facet}s. Return an empty list if no facets of that
    * type were found.
    */
   public Collection<Facet> getFacets();

   /**
    * Return a {@link Collection} of the currently installed {@link Facet}s matching the given type.
    */
   public <F extends Facet> Collection<F> getFacets(Class<F> type);

   /**
    * Install and register the given {@link Facet}. If the facet is already installed, register it instead (See
    * {@link #registerFacet(Facet)}.
    */
   public Project installFacet(Facet facet);

   /**
    * Add the given {@link Facet} to this {@link Project}'s internal collection of installed facets.
    */
   public Project registerFacet(Facet facet);

   /**
    * Remove the given {@link Facet} from this {@link Project}'s internal collection of installed facets.
    */
   public Project unregisterFacet(Facet facet);

   /**
    * Remove the given {@link Facet} from this {@link Project}'s internal collection of installed facets.
    * 
    * @return
    */
   public Project removeFacet(Facet facet);

   /**
    * Get the {@link File} representing the root directory of this {@link Project}
    */
   public DirectoryResource getProjectRoot();

   /**
    * Return true if this project's file-system has been created and initialized; otherwise, return false.
    */
   public boolean exists();
}
