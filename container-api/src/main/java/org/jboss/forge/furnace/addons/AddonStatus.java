/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.addons;

public enum AddonStatus
{
   MISSING, LOADED, STARTED, FAILED;

   public boolean isMissing()
   {
      return this == MISSING;
   }

   public boolean isLoaded()
   {
      return !isMissing();
   }

   public boolean isFailed()
   {
      return this == FAILED;
   }

   public boolean isStarted()
   {
      return this == STARTED || this == FAILED;
   }

}