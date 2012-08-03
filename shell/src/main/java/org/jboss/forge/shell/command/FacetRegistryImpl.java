/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
