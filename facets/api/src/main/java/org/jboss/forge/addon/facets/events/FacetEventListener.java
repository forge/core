/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.facets.events;

import org.jboss.forge.addon.facets.FacetFactory;

/**
 * A listener for {@link FacetEvent} events.
 * 
 * This should be used when an explicit bind to {@link FacetFactory} is needed
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @see FacetFactory#addFacetEventListener(FacetEventListener)
 */
public interface FacetEventListener
{
   void processEvent(FacetEvent event);
}
