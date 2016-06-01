/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic;

import java.util.Arrays;

import org.jboss.forge.addon.projects.AbstractProjectType;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.generic.facets.GenericMetadataFacet;
import org.jboss.forge.addon.projects.generic.facets.GenericProjectFacet;

/**
 * A skeleton class for generic {@link ProjectType} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractGenericProjectType extends AbstractProjectType
{
   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Arrays.<Class<? extends ProjectFacet>> asList(GenericProjectFacet.class, GenericMetadataFacet.class);
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }
}
