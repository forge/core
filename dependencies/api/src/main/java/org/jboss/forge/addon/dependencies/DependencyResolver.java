/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependencies;

import java.util.List;
import java.util.Set;

import org.jboss.forge.furnace.services.Exported;

@Exported
public interface DependencyResolver
{
   /**
    * Resolve a single artifact
    */
   Dependency resolveArtifact(DependencyQuery query);

   /**
    * Resolve all the dependencies from a {@link DependencyQuery} object.
    * 
    * The {@link Dependency} object included inside the {@link DependencyQuery} object is not included in the
    * {@link Set} returned
    */
   Set<Dependency> resolveDependencies(DependencyQuery query);

   /**
    * Resolve the entire dependency hierarchy for the given {@link DependencyQuery}
    */
   DependencyNode resolveDependencyHierarchy(DependencyQuery query);

   /**
    * Resolve the versions from a {@link Dependency} object contained in the {@link DependencyQuery} object.
    */
   List<Coordinate> resolveVersions(DependencyQuery query);
}
