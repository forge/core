/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.util;


import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.furnace.util.Predicate;

/**
 * {@link Predicate} used to filter out SNAPSHOT {@link Dependency} instances.
 */
public class NonSnapshotDependencyFilter implements Predicate<Dependency>
{
   @Override
   public boolean accept(Dependency dependency)
   {
      return dependency != null && !dependency.getCoordinate().isSnapshot();
   }
}