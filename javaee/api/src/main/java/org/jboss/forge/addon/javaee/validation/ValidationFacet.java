/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

/**
 * Facet representing JSR-303 capabilities for Bean Validation
 * 
 * @author Kevin Pollet
 */
@FacetConstraint(ResourcesFacet.class)
public interface ValidationFacet extends JavaEEFacet, Configurable<ValidationConfigurationDescriptor>
{
}
