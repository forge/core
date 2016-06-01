/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic;

import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A generic implementation of {@link ProjectType}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectType extends AbstractGenericProjectType
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
   public String toString()
   {
      return "generic";
   }
}
