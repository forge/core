/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.config;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.projects.Project;
import org.metawidget.config.impl.BaseConfigReader;

/**
 * ConfigReader with Forge-specific features.
 *
 * @author Richard Kennard
 */

public class ForgeConfigReader
         extends BaseConfigReader
{
   //
   // Private statics
   //

   private static final String CONFIG_ELEMENT_NAME = "forgeConfig";

   private static final String PROJECT_ELEMENT_NAME = "forgeProject";

   //
   // Private members
   //

   private Configuration config;

   private Project project;

   //
   // Constructor
   //

   public ForgeConfigReader(Configuration config, Project project)
   {
      this.config = config;
      this.project = project;
   }

   //
   // Protected methods
   //

   @Override
   protected boolean isNative(String name)
   {
      if (PROJECT_ELEMENT_NAME.equals(name))
      {
         return true;
      }

      if (CONFIG_ELEMENT_NAME.equals(name))
      {
         return true;
      }

      return super.isNative(name);
   }

   @Override
   protected Object createNative(String name, Class<?> namespace, String recordedText) throws Exception
   {
      if (PROJECT_ELEMENT_NAME.equals(name))
      {
         return this.project;
      }

      if (CONFIG_ELEMENT_NAME.equals(name))
      {
         return this.config;
      }

      return super.createNative(name, namespace, recordedText);
   }
}
