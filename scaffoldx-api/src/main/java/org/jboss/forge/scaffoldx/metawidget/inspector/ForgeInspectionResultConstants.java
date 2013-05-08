/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.metawidget.inspector;

/**
 * Forge-specific element and attribute names appearing in DOMs conforming to inspection-result-1.0.xsd.
 *
 * @author Richard Kennard
 */

public final class ForgeInspectionResultConstants
{
   //
   // Public statics
   //

   public static final String N_TO_MANY = "n-to-many";

   public static final String ONE_TO_ONE = "one-to-one";
   
   public static final String MANY_TO_ONE = "many-to-one";

   /**
    * Whether the field is an Id.
    */

   public static final String PRIMARY_KEY = "primary-key";

   /**
    * The reverse primary key of a ManyToOne relationship.
    */

   public static final String REVERSE_PRIMARY_KEY = "reverse-primary-key";

   //
   // Private constructor
   //

   private ForgeInspectionResultConstants()
   {
      // Can never be called
   }
}
