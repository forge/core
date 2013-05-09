/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.javaee.spec;

import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

public interface CDIFacet extends ProjectFacet
{
   /**
    * Parse and return this {@link Project}'s beans.xml file as a {@link BeansDescriptor}
    */
   BeansDescriptor getConfig();

   /**
    * Save the given {@link BeansDescriptor} as this {@link Project}'s beans.xml file
    */
   void saveConfig(final BeansDescriptor model);

   /**
    * Get a reference to this {@link Project}'s beans.xml file.
    */
   FileResource<?> getConfigFile();
}
