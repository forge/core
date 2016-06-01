/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets.events;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.Faceted;

/**
 * An event describing the registration of a {@link Facet} on a {@link Faceted}.
 * 
 * Facets may be registered when their installation requirements have already been met
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface FacetRegistered extends FacetEvent
{
}
