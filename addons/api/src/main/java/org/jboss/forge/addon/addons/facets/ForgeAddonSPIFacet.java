/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.facets;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * Configures the project as an Addon SPI project
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ForgeAddonSPIFacet extends AbstractForgeAddonFacet
{

   @SuppressWarnings("unchecked")
   @Override
   protected List<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Arrays.<Class<? extends ProjectFacet>> asList(JavaSourceFacet.class, ForgeAddonFacet.class);
   }

}