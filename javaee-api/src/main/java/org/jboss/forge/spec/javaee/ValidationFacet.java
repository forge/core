/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;

/**
 * Facet representing JSR-303 capabilities for Bean Validation
 * 
 * @author Kevin Pollet
 */
public interface ValidationFacet extends Facet
{
   ValidationDescriptor getConfig();

   FileResource<?> getConfigFile();

   void saveConfig(ValidationDescriptor descriptor);
}
