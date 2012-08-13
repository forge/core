/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import java.beans.BeanDescriptor;

import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.FileResource;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;

/**
 * If installed, this {@link Project} supports features from the CDI specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CDIFacet extends Facet
{
   /**
    * Parse and return this {@link Project}'s beans.xml file as a {@link BeansDescriptor}
    */
   BeansDescriptor getConfig();

   /**
    * Save the given {@link BeanDescriptor} as this {@link Project}'s beans.xml file
    */
   void saveConfig(final BeansDescriptor model);

   /**
    * Get a reference to this {@link Project}'s beans.xml file.
    */
   FileResource<?> getConfigFile();
}
