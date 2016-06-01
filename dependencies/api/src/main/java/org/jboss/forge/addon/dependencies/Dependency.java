/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import java.util.List;

import org.jboss.forge.addon.resource.FileResource;

/**
 * Represents a project library dependency.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Dependency
{
   /**
    * Get the {@link Coordinate} for this {@link Dependency}
    */
   Coordinate getCoordinate();

   /**
    * Get the scope type of this {@link Dependency}
    */
   String getScopeType();

   /**
    * Returns the file representing this artifact
    */
   FileResource<?> getArtifact() throws DependencyException;

   /**
    * Returns if this dependency is optional
    */
   boolean isOptional();

   /**
    * Returns the set of {@link Coordinate} instances that are excluded from this {@link Dependency} child hierarchy.
    */
   List<Coordinate> getExcludedCoordinates();

}
