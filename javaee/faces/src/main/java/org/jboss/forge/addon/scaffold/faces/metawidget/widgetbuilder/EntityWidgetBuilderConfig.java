/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.widgetbuilder;

import org.jboss.forge.addon.configuration.Configuration;
import org.metawidget.util.simple.ObjectUtils;

/**
 * Configures a <tt>ForgePropertyStyle</tt>.
 *
 * @author Richard Kennard
 */

public class EntityWidgetBuilderConfig
{

   //
   // Private members
   //

   private Configuration config;

   //
   // Public methods
   //

   public EntityWidgetBuilderConfig setConfig(Configuration config)
   {
      this.config = config;
      return this;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }

      if (!ObjectUtils.nullSafeClassEquals(this, that))
      {
         return false;
      }

      if (this.config != ((EntityWidgetBuilderConfig) that).config)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      return ObjectUtils.nullSafeHashCode(this.config);
   }

   //
   // Protected methods
   //

   protected Configuration getConfig()
   {
      return this.config;
   }
}
