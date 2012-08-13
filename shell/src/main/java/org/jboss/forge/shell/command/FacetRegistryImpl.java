/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.project.Facet;
import org.jboss.forge.shell.project.FacetRegistry;

/**
 * Stores the current registry of all installed & loaded facets.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class FacetRegistryImpl implements FacetRegistry
{
   private Set<Class<? extends Facet>> facetTypes;

   private final CommandLibraryExtension library;

   @Inject
   public FacetRegistryImpl(final CommandLibraryExtension library)
   {
      this.library = library;
   }

   @PostConstruct
   public void init()
   {
      facetTypes = library.getFacetTypes();
   }

   /**
    * @return the facetTypes
    */
   @Override
   public Set<Class<? extends Facet>> getFacetTypes()
   {
      return facetTypes;
   }

}
