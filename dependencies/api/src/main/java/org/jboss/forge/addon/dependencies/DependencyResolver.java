/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DependencyResolver
{
   /**
    * Resolve a single {@link Dependency} matching the given {@link DependencyQuery}.
    */
   Dependency resolveArtifact(DependencyQuery query);

   /**
    * Resolve all dependencies for the {@link Dependency} resolved by the given {@link DependencyQuery} object.
    * 
    * The {@link Dependency} object included inside the {@link DependencyQuery} object is not included in the
    * {@link Set} returned
    */
   Set<Dependency> resolveDependencies(DependencyQuery query);

   /**
    * Resolve the entire dependency hierarchy for the given {@link DependencyQuery}. Return results as a graph of
    * {@link DependencyNode} instances.
    */
   DependencyNode resolveDependencyHierarchy(DependencyQuery query);

   /**
    * Resolve the versions from a {@link Dependency} object contained in the {@link DependencyQuery} object in ascending
    * order.
    */
   List<Coordinate> resolveVersions(DependencyQuery query);

   /**
    * Resolve {@link DependencyMetadata} for a given {@link DependencyQuery}, searching the default repository. This
    * returns information about the configured repositories, dependencies, and managed dependencies of the given query.
    * <p>
    * Note: This method does not accept version ranges. A single version must be specified.
    * <p>
    * <b>Valid query version:</b> 1.0 <b><br>
    * Invalid query version:</b> [1.0,2.0]
    */
   DependencyMetadata resolveDependencyMetadata(DependencyQuery query);
}
