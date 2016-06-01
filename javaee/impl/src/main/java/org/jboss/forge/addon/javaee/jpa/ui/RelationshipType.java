/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

public enum RelationshipType
{
   BASIC("Basic"),
   EMBEDDED("Embedded"),
   ONE_TO_ONE("One-to-One"),
   ONE_TO_MANY("One-to-Many"),
   MANY_TO_ONE("Many-to-One"),
   MANY_TO_MANY("Many-to-Many");

   private RelationshipType(String description)
   {
      this.description = description;
   }

   private String description;

   public String getDescription()
   {
      return description;
   }
}
