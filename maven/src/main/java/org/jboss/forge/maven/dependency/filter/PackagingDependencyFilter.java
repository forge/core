/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency.filter;

import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyFilter;

/**
 * Filters a set of {@link Dependency} objects by its packaging
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class PackagingDependencyFilter implements DependencyFilter
{
   private String packaging;

   public PackagingDependencyFilter(String packaging)
   {
      super();
      if (packaging == null || packaging.isEmpty())
      {
         throw new IllegalArgumentException("Packaging should not be null/empty");
      }
      this.packaging = packaging;
   }

   public String getPackaging()
   {
      return packaging;
   }

   @Override
   public boolean accept(Dependency dependency)
   {
      return packaging.equalsIgnoreCase(dependency.getCoordinate().getPackaging());
   }

}
