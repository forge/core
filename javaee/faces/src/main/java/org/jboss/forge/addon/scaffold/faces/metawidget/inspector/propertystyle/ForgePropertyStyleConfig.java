/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspector.propertystyle;

import java.text.MessageFormat;

import org.jboss.forge.addon.projects.Project;
import org.metawidget.inspector.impl.propertystyle.javabean.JavaBeanPropertyStyleConfig;
import org.metawidget.util.simple.ObjectUtils;

/**
 * Configures a <tt>ForgePropertyStyle</tt>.
 *
 * @author Richard Kennard
 */

public class ForgePropertyStyleConfig
         extends JavaBeanPropertyStyleConfig
{

   //
   // Private members
   //

   private Project project;

   //
   // Public methods
   //

   public ForgePropertyStyleConfig setProject(Project project)
   {
      this.project = project;
      return this;
   }

   /**
    * Overridden to use covariant return type.
    *
    * @return this, as part of a fluent interface
    */

   @Override
   public ForgePropertyStyleConfig setPrivateFieldConvention(MessageFormat privateFieldConvention)
   {
      super.setPrivateFieldConvention(privateFieldConvention);

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

      if (this.project != ((ForgePropertyStyleConfig) that).project)
      {
         return false;
      }

      return super.equals(that);
   }

   @Override
   public int hashCode()
   {

      int hashCode = super.hashCode();
      hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.project);

      return hashCode;
   }

   //
   // Protected methods
   //

   protected Project getProject()
   {
      return this.project;
   }

   /**
    * Overridden so that is exposed to ForgePropertyStyle.
    */

   @Override
   protected MessageFormat getPrivateFieldConvention()
   {
      return super.getPrivateFieldConvention();
   }
}
