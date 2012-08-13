/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.List;

import org.jboss.forge.project.packaging.PackagingType;

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
    * Get the specified packaging type of this {@link Dependency}
    */
   String getPackagingType();

   /**
    * Get the {@link PackagingType} of this {@link Dependency}, if the type is not one of the default supported types,
    * {@link PackagingType#OTHER} will be returned;
    */
   PackagingType getPackagingTypeEnum();

   /**
    * Get the scope type of this {@link Dependency}
    */
   String getScopeType();

   /**
    * Get the {@link ScopeType} of this {@link Dependency}, if the type is not one of the default supported types,
    * {@link ScopeType#OTHER} will be returned;
    */
   ScopeType getScopeTypeEnum();

   /**
    * Get a list of {@link Dependency} objects to be excluded from this {@link Dependency}'s list of inclusions when it
    * is included in a project.
    */
   List<Dependency> getExcludedDependencies();

   /**
    * Return a string represenging this dependency in the form of a standard identifier. E.g:
    * "groupId : artifactId : version"
    */
   String toCoordinates();

   /**
    * Returns true if this dependency version is a SNAPSHOT
    */
   boolean isSnapshot();

}
