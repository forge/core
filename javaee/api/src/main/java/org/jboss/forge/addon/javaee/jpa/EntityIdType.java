/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

/**
 * Describes what kind of ID an Entity uses.
 *
 * @author <a href="mailto:ch.schulz@joinout.de">Christoph "criztovyl" Schulz</a>
 */
public enum EntityIdType
{
   /**
    * Uses a {@link Long}.
    */
   LONG_PROPERTY,
   /**
    * Use an {@link javax.persistence.IdClass}.
    */
   ID_CLASS,
   /**
    * Use an {@link javax.persistence.EmbeddedId}.
    */
   EMBEDDED_ID;

   /**
    * @return Whether an additional class is required to implement this ID type.
    */
   public boolean isClassRequired()
   {
      return this != LONG_PROPERTY;
   }
}
