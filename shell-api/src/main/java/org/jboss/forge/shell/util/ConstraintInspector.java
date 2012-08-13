/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.bus.util.Annotations;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.shell.plugins.RequiresProject;

/**
 * Used to inspect types that may or may not depend on {@link Facet}s or {@link PackagingType}s
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ConstraintInspector
{
   /**
    * Return the name of the given bean type.
    */
   public static String getName(final Class<?> type)
   {
      String result = type.getSimpleName();

      if (Annotations.isAnnotationPresent(type, Alias.class))
      {
         Alias annotation = Annotations.getAnnotation(type, Alias.class);
         if ((annotation.value() != null) && !annotation.value().trim().isEmpty())
         {
            result = annotation.value();
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} for any dependencies to {@link Facet} types.
    */
   public static List<Class<? extends Facet>> getFacetDependencies(final Class<?> type)
   {
      List<Class<? extends Facet>> result = new ArrayList<Class<? extends Facet>>();

      if (Annotations.isAnnotationPresent(type, RequiresFacet.class))
      {
         RequiresFacet requires = Annotations.getAnnotation(type, RequiresFacet.class);
         if (requires.value() != null)
         {
            result.addAll(Arrays.asList(requires.value()));
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} for any dependencies to {@link PackagingType} types.
    */
   public static List<PackagingType> getCompatiblePackagingTypes(final Class<?> type)
   {
      List<PackagingType> result = new ArrayList<PackagingType>();

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

   /**
    * Inspect the given {@link Class} type for a dependency on an active project.
    */
   public static boolean requiresProject(final Class<?> type)
   {
      return Annotations.isAnnotationPresent(type, RequiresProject.class);
   }
}
