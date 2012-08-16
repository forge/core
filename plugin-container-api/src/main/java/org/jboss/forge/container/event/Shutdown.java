/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.event;

/**
 * Fired when the container begins its shutdown process.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class Shutdown
{
   private final Status status;

   /**
    * Status of the shell during shutdown.
    */
   public enum Status
   {
      /**
       * The shell is shutting down normally.
       */
      NORMAL,
      /**
       * A fatal error has forced the shell to shut down.
       */
      ERROR
   }

   /**
    * Defaults to {@link Status#NORMAL}
    */
   public Shutdown()
   {
      this.status = Status.NORMAL;
   }

   /**
    * Inform the shell to shut down with the given {@link Status}, now.
    */
   public Shutdown(final Status status)
   {
      this.status = status;
   }

   /**
    * Get the status with which the shell should shut down.
    */
   public Status getStatus()
   {
      return status;
   }
}
