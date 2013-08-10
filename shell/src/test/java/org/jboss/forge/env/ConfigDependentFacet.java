/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.env;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("test.config.facet")
@RequiresFacet(ConfigurationFacet.class)
public class ConfigDependentFacet extends BaseFacet
{

   private Configuration projectConfiguration;

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      projectConfiguration = this.getProject().getFacet(ConfigurationFacet.class).getConfiguration();
      return false;
   }

   public Configuration getProjectConfiguration()
   {
      return projectConfiguration;
   }

}
