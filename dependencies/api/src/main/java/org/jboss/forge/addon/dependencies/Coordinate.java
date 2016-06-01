/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies;

/**
 * Coordinates for a single {@link Dependency}.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Coordinate
{
   /**
    * Get the major identifier
    */
   String getGroupId();

   /**
    * Get the minor-identifier
    */
   String getArtifactId();

   /**
    * Get the version
    */
   String getVersion();

   /**
    * Get the classifier. May be null
    */
   String getClassifier();

   /**
    * Get the packaging type. Defaults to "jar"
    */
   String getPackaging();

   /**
    * Get the systemPath of this {@link Dependency}
    */
   String getSystemPath();

   /**
    * Returns true if this coordinate version is a SNAPSHOT
    */
   boolean isSnapshot();
}
