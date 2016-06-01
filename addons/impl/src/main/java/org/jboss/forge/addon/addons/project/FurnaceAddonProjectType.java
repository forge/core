/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.project;

import java.util.Arrays;

import org.jboss.forge.addon.addons.ui.FurnaceAddonSetupStep;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.AbstractProjectType;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FurnaceAddonProjectType extends AbstractProjectType
{
   @Override
   public String getType()
   {
      return "Forge Addon (JAR)";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return FurnaceAddonSetupStep.class;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Arrays.<Class<? extends ProjectFacet>> asList(MavenPluginFacet.class);
   }

   @Override
   public String toString()
   {
      return "forge-addon";
   }

   @Override
   public int priority()
   {
      return 500;
   }
}
