/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

/**
 * Skeleton class for {@link ProjectProvider} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractProjectProvider implements ProjectProvider
{
   @Override
   public Class<? extends ProjectFacet> resolveProjectFacet(Class<? extends ProjectFacet> facet)
   {
      return facet;
   }
}