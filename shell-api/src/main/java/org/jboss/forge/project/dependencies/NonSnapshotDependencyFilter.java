/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

/**
 * Filters non-snapshots dependencies
 * 
 * @author George Gastaldi <george.gastaldi@redhat.com>
 * 
 */
public class NonSnapshotDependencyFilter implements DependencyFilter
{
   @Override
   public boolean accept(Dependency dependency)
   {
      return dependency != null && !dependency.isSnapshot();
   }
}
