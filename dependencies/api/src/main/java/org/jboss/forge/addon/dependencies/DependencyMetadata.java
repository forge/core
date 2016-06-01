/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

import java.util.List;

/**
 * Represents meta-information for a given {@link Dependency} including its dependency chain, configured
 * {@link DependencyRepository} instances, and any managed dependency version information.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DependencyMetadata
{
   /**
    * The {@link Dependency} for which this {@link DependencyMetadata} was retrieved.
    */
   Dependency getDependency();

   /**
    * The managed dependencies of the {@link Dependency} for which this {@link DependencyMetadata} was retrieved. These
    * dependencies are not included in downstream projects unless also specified as a direct dependency.
    * 
    * @see {@link #getDependencies()}
    */
   List<Dependency> getManagedDependencies();

   /**
    * The direct dependencies of the {@link Dependency} for which this {@link DependencyMetadata} was retrieved. These
    * dependencies are included in downstream projects unless explicitly excluded.
    * 
    * @see {@link Dependency#getExcludedDependencies()}
    */
   List<Dependency> getDependencies();

   /**
    * The {@link DependencyRepository} instances used when building the {@link Dependency}, or any projects which
    * inherit from it, for which this {@link DependencyMetadata} was retrieved.
    */
   List<DependencyRepository> getRepositories();
}