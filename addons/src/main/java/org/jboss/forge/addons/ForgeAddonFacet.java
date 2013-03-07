/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addons;

import org.jboss.forge.facets.BaseFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ForgeAddonFacet extends BaseFacet<Project> implements ProjectFacet
{
   @Override
   public boolean install()
   {
      return false;
   }

   @Override
   public boolean isInstalled()
   {
      return false;
   }

   @Override
   public void setOrigin(Project origin)
   {
      super.setOrigin(origin);
   }
}
