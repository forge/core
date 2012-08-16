/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.event;

/**
 * Fired before the container begins its shutdown process.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class PreShutdown
{
   private final Shutdown.Status status;

   public PreShutdown()
   {
      this.status = Shutdown.Status.NORMAL;
   }

   public PreShutdown(final Shutdown.Status status)
   {
      this.status = status;
   }

   /**
    * Get the status with which the shell is shutting down.
    */
   public Shutdown.Status getStatus()
   {
      return status;
   }
}
