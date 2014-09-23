/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

/**
 * The possible schema generation types
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum SchemaGenerationType
{
   /**
    * Any artifacts in the database will be deleted, and the provider will create the database artifacts on deployment.
    */
   DROP_CREATE("Drop and Create",
            "Any artifacts in the database will be deleted, and the provider will create the database artifacts on deployment."),
   /**
    * Any artifacts in the database will be deleted on application deployment.
    */
   DROP("Drop", "Any artifacts in the database will be deleted on application deployment"),
   /**
    * The provider will create the database artifacts on application deployment. The artifacts will remain unchanged
    * after application redeployment.
    */
   CREATE(
            "Create",
            "The provider will create the database artifacts on application deployment. The artifacts will remain unchanged after application redeployment"),
   /**
    * No schema creation or deletion will take place.
    */
   NONE("None", "No schema creation or deletion will take place");

   private final String label;
   private final String description;

   private SchemaGenerationType(String label, String description)
   {
      this.label = label;
      this.description = description;
   }

   public String getLabel()
   {
      return label;
   }

   public String getDescription()
   {
      return description;
   }

   /**
    * Does this action include creations?
    *
    * @return {@code true} if this action is either {@link #CREATE} or {@link #DROP_CREATE}
    */
   public boolean includesCreate()
   {
      return this == CREATE || this == DROP_CREATE;
   }

   /**
    * Does this action include drops?
    *
    * @return {@code true} if this action is either {@link #DROP} or {@link #DROP_CREATE}
    */
   public boolean includesDrop()
   {
      return this == DROP || this == DROP_CREATE;
   }
}
