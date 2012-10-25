/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.dependency;

/**
 * Represents a project library dependency.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Dependency
{
   /**
    * Get the minor-identifier for this {@link Dependency}.
    */
   String getArtifactId();

   /**
    * Get the classifier for this {@link Dependency}
    */
   String getClassifier();

   /**
    * Get the major identifier for this {@link Dependency}.
    */
   String getGroupId();

   /**
    * Get the version of this {@link Dependency}.
    */
   String getVersion();

   /**
    * Get the systemPath of this {@link Dependency}
    */
   String getSystemPath();

   /**
    * Get the specified packaging type of this {@link Dependency}
    */
   String getPackagingType();

   /**
    * Get the scope type of this {@link Dependency}
    */
   String getScopeType();

   /**
    * Return a string representing this dependency in the form of a standard identifier. E.g:
    * "groupId : artifactId : version"
    */
   String toCoordinates();

   /**
    * Returns true if this dependency version is a SNAPSHOT
    */
   boolean isSnapshot();

   /**
    * Returns if this dependency is optional
    */
   boolean isOptional();

}
