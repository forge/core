/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle;

import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * A stubbed out implementation of a Project
 */
public class StubProject implements Project
{
   @Override
   public Object getAttribute(Object key)
   {
      return null;
   }

   @Override
   public void setAttribute(Object key, Object value)
   {

   }

   @Override
   public void removeAttribute(Object key)
   {

   }

   @Override
   public DirectoryResource getRootDirectory()
   {
      return null;
   }

   @Override
   public Resource<?> getRoot()
   {
      return null;
   }

   @Override
   public boolean hasFacet(Class<? extends ProjectFacet> type)
   {
      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean hasAllFacets(Class<? extends ProjectFacet>... facetDependencies)
   {
      return false;
   }

   @Override
   public boolean hasAllFacets(Iterable<Class<? extends ProjectFacet>> facetDependencies)
   {
      return false;
   }

   @Override
   public <F extends ProjectFacet> F getFacet(Class<F> type) throws FacetNotFoundException
   {
      return null;
   }

   @Override
   public Iterable<ProjectFacet> getFacets()
   {
      return null;
   }

   @Override
   public <F extends ProjectFacet> Iterable<F> getFacets(Class<F> type)
   {
      return null;
   }

   @Override
   public <F extends ProjectFacet> boolean supports(F facet)
   {
      return false;
   }
}
