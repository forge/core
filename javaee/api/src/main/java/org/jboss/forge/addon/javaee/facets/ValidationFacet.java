/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.facets;

import org.jboss.forge.addon.javaee.ConfigurableFacet;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Facet representing JSR-303 capabilities for Bean Validation
 * 
 * @author Kevin Pollet
 */
public interface ValidationFacet extends JavaEEFacet, ConfigurableFacet<ValidationDescriptor>
{
   ValidationDescriptor getConfig();

   FileResource<?> getConfigFile();

   void saveConfig(ValidationDescriptor descriptor);
}
