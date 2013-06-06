/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.converter;

import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class TestProjectType implements ProjectType
{

   @Override
   public String getType()
   {
      return "Test Project";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return null;
   }

   @Override
   public String toString()
   {
      return "test";
   }

}
