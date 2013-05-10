/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addons.facets;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.projects.ProjectFacet;

/**
 * Configures the project as an Addon Test project
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeAddonTestFacet extends AbstractForgeAddonFacet
{

   @Override
   @SuppressWarnings("unchecked")
   protected List<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Arrays.<Class<? extends ProjectFacet>>asList();
   }


}