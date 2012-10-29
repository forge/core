/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;

import java.util.Set;

public interface DependencyResolver
{
   /**
    * Resolve all the dependencies from a {@link Dependency} object.
    *
    * The {@link Dependency} object included inside the {@link DependencyQuery} object is not included in the
    * {@link Set} returned
    *
    * @param query
    * @return
    */
   Set<Dependency> resolveDependencies(DependencyQuery query);
}
