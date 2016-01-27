/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import java.util.Collections;
import java.util.Set;

import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.furnace.util.Sets;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NoStack implements Stack
{
   public static final NoStack INSTANCE = new NoStack();

   private NoStack()
   {
   }

   @Override
   public String getName()
   {
      return "None";
   }

   @Override
   public boolean supports(Class<? extends ProjectFacet> facet)
   {
      return true;
   }

   @Override
   public boolean matches(Class<? extends ProjectFacet> facet)
   {
      return true;
   }

   @Override
   public <T extends ProjectFacet> Set<T> filter(Class<T> type, Iterable<T> facets)
   {
      return Sets.toSet(facets);
   }

   @Override
   public Set<Class<? extends ProjectFacet>> getIncludedFacets()
   {
      return Collections.emptySet();
   }

   @Override
   public Set<Class<? extends ProjectFacet>> getExcludedFacets()
   {
      return Collections.emptySet();
   }
}
