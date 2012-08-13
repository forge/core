/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.env;

import javax.inject.Inject;

import org.jboss.forge.project.facets.BaseFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockConfigFacet extends BaseFacet
{
   static final String INSTALLED = "MCF_INSTALLED";
   @Inject
   private Configuration config;

   @Override
   public boolean install()
   {
      try
      {
         Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);
         projectConfig.setProperty(INSTALLED, true);
      }
      catch (Exception e)
      {
         return false;
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return config.getProperty(INSTALLED) != null;
   }

}
