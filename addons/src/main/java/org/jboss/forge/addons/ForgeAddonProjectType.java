/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectType;
import org.jboss.forge.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ForgeAddonProjectType implements ProjectType
{
   @Override
   public String getType()
   {
      return "Forge Addon";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return ForgeAddonSetupStep.class;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      List<Class<? extends ProjectFacet>> result = new ArrayList<Class<? extends ProjectFacet>>();
      result.add(ForgeAddonFacet.class);
      return result;
   }
}
