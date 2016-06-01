/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.events;

import org.jboss.forge.addon.facets.AbstractFaceted;
import org.jboss.forge.addon.facets.Facet;

public class MockFaceted extends AbstractFaceted<Facet<?>>
{

   @Override
   public <F extends Facet<?>> boolean supports(F facet)
   {
      return true;
   }

}