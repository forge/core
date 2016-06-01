/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.util;

import java.util.Arrays;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Composite {@link Predicate<Dependency>} implementation for handling multiple filters.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CompositeDependencyFilter implements Predicate<Dependency>
{
   private Iterable<? extends Predicate<Dependency>> filters;

   @SafeVarargs
   public CompositeDependencyFilter(Predicate<Dependency>... filters)
   {
      this.filters = Arrays.asList(filters);
   }

   public CompositeDependencyFilter(Iterable<? extends Predicate<Dependency>> filters)
   {
      this.filters = filters;
   }

   /**
    * Tests each filter if it matches the dependency informed. If any of the {@link Predicate#accept(Dependency)}
    * objects contained in this class returns false, false is immediately returned from this method.
    */
   @Override
   public boolean accept(Dependency dependency)
   {
      for (Predicate<Dependency> filter : filters)
      {
         if (!filter.accept(dependency))
         {
            return false;
         }
      }
      return true;
   }
}
