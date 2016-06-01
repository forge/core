/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype;

import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A base class for custom project types defined by an archetype
 */
public abstract class CustomMavenArchetypeProjectType extends MavenArchetypeProjectType
{
   private final String type;
   private final Class<? extends UIWizardStep> flowStep;

   public CustomMavenArchetypeProjectType(Class<? extends UIWizardStep> flowStep, String type)
   {
      this.flowStep = flowStep;
      this.type = type;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return flowStep;
   }

   @Override
   public String toString()
   {
      return type.toLowerCase().replace(' ', '-');
   }
}
