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
   private final ExitStatus status;

   /**
    * ExitStatus of the container during shutdown.
    */
   public enum ExitStatus
   {
      /**
       * The container was shut down normally.
       */
      NORMAL,

      /**
       * A fatal error has forced the container to shut down.
       */
      ERROR
   }

   /**
    * Defaults to {@link ExitStatus#NORMAL}
    */
   public Shutdown()
   {
      this.status = ExitStatus.NORMAL;
   }

   /**
    * Inform the container to shut down with the given {@link ExitStatus}, now.
    */
   public Shutdown(final ExitStatus status)
   {
      this.status = status;
   }

   /**
    * Get the status with which the container should shut down.
    */
   public ExitStatus getStatus()
   {
      return status;
   }
}
