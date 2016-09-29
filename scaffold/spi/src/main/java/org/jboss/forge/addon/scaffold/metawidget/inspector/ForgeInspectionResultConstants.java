/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.metawidget.inspector;

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
   
   public static final String JPA_ONE_TO_ONE = "one-to-one";
   
   public static final String JPA_MANY_TO_ONE = "many-to-one";
   
   public static final String JPA_ONE_TO_MANY = "one-to-many";
   
   public static final String JPA_MANY_TO_MANY = "many-to-many";
   
   public static final String JPA_REL_TYPE = "jpa-relation-type";

   /**
    * Whether the field is an Id.
    */

   public static final String PRIMARY_KEY = "primary-key";

   /**
    * The reverse primary key of a ManyToOne relationship.
    */

   public static final String REVERSE_PRIMARY_KEY = "reverse-primary-key";
   
   /**
    * Whether the field represents a generated value
    */
   public static final String GENERATED_VALUE = "generated-value";
   
   /**
    * The owning field of a bi-directional relationship
    */
   public static final String OWNING_FIELD = "owning-field";
   
   /**
    * The inverse field of a bi-directional relationship
    */
   public static final String INVERSE_FIELD = "inverse-field";
   
   /**
    * Indicates whether a field is to be displayed inline or not.
    * Usually used for JPA Embedded/Embeddable types. 
    */
   public static final String EMBEDDABLE = "embeddable";

   //
   // Private constructor
   //

   private ForgeInspectionResultConstants()
   {
      // Can never be called
   }
}
