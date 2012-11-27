/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.io.File;

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
    * Get the systemPath of this {@link Dependency}
    */
   String getSystemPath();

   /**
    * Get the scope type of this {@link Dependency}
    */
   String getScopeType();

   /**
    * Returns true if this dependency version is a SNAPSHOT
    */
   boolean isSnapshot();

   /**
    * Returns the file representing this artifact
    *
    */
   File getArtifact();

   /**
    * Returns if this dependency is optional
    */
   boolean isOptional();

}
