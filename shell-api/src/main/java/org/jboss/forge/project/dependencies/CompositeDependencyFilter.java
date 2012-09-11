/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.Arrays;

/**
 * Composite {@link DependencyFilter} implementation for handling multiple filters.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class CompositeDependencyFilter implements DependencyFilter
{
   private Iterable<? extends DependencyFilter> filters;

   public CompositeDependencyFilter(DependencyFilter... filters)
   {
      this.filters = Arrays.asList(filters);
   }

   public CompositeDependencyFilter(Iterable<? extends DependencyFilter> filters)
   {
      this.filters = filters;
   }

   /**
    * Tests each filter if it matches the dependency informed. If any of the {@link DependencyFilter#accept(Dependency)}
    * objects contained in this class returns false, false is immediately returned from this method.
    */
   @Override
   public boolean accept(Dependency dependency)
   {
      for (DependencyFilter filter : filters)
      {
         if (!filter.accept(dependency))
         {
            return false;
         }
      }
      return true;
   }
}
