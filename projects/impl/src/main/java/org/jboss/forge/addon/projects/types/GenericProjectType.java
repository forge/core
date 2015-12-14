/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.types;

import java.util.Arrays;

import org.jboss.forge.addon.projects.AbstractProjectType;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.facets.generic.GenericMetadataFacet;
import org.jboss.forge.addon.projects.facets.generic.GenericProjectFacet;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A generic implementation of {@link ProjectType}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectType extends AbstractProjectType
{
   @Override
   public String getType()
   {
      return "Generic";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      // no extra setup steps required
      return null;
   }

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

   @Override
   public String toString()
   {
      return "generic";
   }

}
