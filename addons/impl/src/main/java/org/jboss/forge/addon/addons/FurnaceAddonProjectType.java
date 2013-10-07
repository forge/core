/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons;

import java.util.Collections;

import org.jboss.forge.addon.projects.BuildSystemFacet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FurnaceAddonProjectType implements ProjectType
{
   @Override
   public String getType()
   {
      return "Forge Addon";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return FurnaceAddonSetupStep.class;
   }

   @Override
   public Iterable<Class<? extends BuildSystemFacet>> getRequiredBuildSystemFacets()
   {
      return Collections.emptySet();
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Collections.emptySet();
   }

   @Override
   public String toString()
   {
      return "addon";
   }
}
