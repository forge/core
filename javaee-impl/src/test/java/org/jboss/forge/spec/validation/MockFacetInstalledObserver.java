/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.events.FacetInstalled;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class MockFacetInstalledObserver
{
   private Facet observed = null;

   public void observe(@Observes final FacetInstalled event)
   {
      observed = event.getFacet();
   }

   public Facet observed()
   {
      return observed;
   }
}
