/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

/**
 *
 * Allows to locate a specific artifact.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
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
    * Get the packaging type. Defaults to "far"
    */
   String getPackaging();
}
