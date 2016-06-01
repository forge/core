/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import java.util.List;

import org.jboss.forge.furnace.util.Predicate;

/**
 * A parameter object which is used to search dependencies
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface DependencyQuery
{
   /**
    * Return the {@link Coordinate} pattern for this query.
    */
   Coordinate getCoordinate();

   /**
    * Return the scope type restriction for this query.
    */
   String getScopeType();

   /**
    * Return the {@link Dependency} filter {@link Predicate} for this {@link DependencyQuery}.
    */
   Predicate<Dependency> getDependencyFilter();

   /**
    * Return the {@link DependencyRepository} instances to be queried against. If none are provided, use system default
    * or externally-configured repositories.
    */
   List<DependencyRepository> getDependencyRepositories();

}