/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.repositories;

/**
 * A set of modes for interacting with an {@link AddonRepository}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum AddonRepositoryMode
{
   /**
    * A read-write repository.
    */
   MUTABLE,

   /**
    * A read-only repository.
    */
   IMMUTABLE;

   /**
    * Return <code>true</code> if this {@link AddonRepositoryMode} instance is {@link AddonRepositoryMode#MUTABLE}
    */
   public boolean isMutable()
   {
      return this == MUTABLE;
   }

   /**
    * Return <code>true</code> if this {@link AddonRepositoryMode} instance is {@link AddonRepositoryMode#IMMUTABLE}
    */
   public boolean isImmutable()
   {
      return this == IMMUTABLE;
   }
}
