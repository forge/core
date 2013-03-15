/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

public enum Status
{
   MISSING, LOADED, STARTED, STOPPED, FAILED;

   public boolean isMissing()
   {
      return this == MISSING;
   }

   public boolean isLoaded()
   {
      return this == LOADED;
   }

   public boolean isFailed()
   {
      return this == FAILED;
   }

   public boolean isStarted()
   {
      return this == STARTED;
   }

   public boolean isStopped()
   {
      return this == STOPPED || this == FAILED;
   }

}