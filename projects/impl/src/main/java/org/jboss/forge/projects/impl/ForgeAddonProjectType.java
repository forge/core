/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects.impl;

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
}
