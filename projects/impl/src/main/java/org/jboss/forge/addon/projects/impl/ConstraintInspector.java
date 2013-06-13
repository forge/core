/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.RequiresPackagingType;
import org.jboss.forge.furnace.util.Annotations;

/**
 * Used to inspect types that may or may not depend on {@link Facet}s or packaging types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ConstraintInspector
{

   /**
    * Inspect the given {@link Class} for any dependencies to packaging types.
    */
   public static List<String> getCompatiblePackagingTypes(final Class<? extends ProjectFacet> type)
   {
      List<String> result = new ArrayList<String>();

      if (Annotations.isAnnotationPresent(type, RequiresPackagingType.class))
      {
         RequiresPackagingType requires = Annotations.getAnnotation(type, RequiresPackagingType.class);
         if (requires.value() != null)
         {
            result.addAll(Arrays.asList(requires.value()));
         }
      }

      return result;
   }

}