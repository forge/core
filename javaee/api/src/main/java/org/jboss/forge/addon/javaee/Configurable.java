/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

import javax.management.Descriptor;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.resource.FileResource;

/**
 * This interface should be implemented by {@link Facet}s that supports configuration descriptors
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Configurable<DESCRIPTOR>
{
   /**
    * Return the {@link Descriptor} of the specification for which this facet represents.
    */
   DESCRIPTOR getConfig();

   /**
    * Returns the {@link FileResource} of the descriptor
    */
   FileResource<?> getConfigFile();

   /**
    * Persists the current {@link Descriptor}
    */
   void saveConfig(DESCRIPTOR descriptor);
}
