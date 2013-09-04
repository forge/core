/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi;

import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * If installed, this {@link Project} supports features from the CDI specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface CDIFacet extends JavaEEFacet
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
