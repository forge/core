/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.events;

import org.jboss.forge.shell.Shell;

/**
 * Fired in response to receipt of the shell {@link Shutdown} command event. This event must be fired before the
 * {@link Shell} continues shutdown procedures.
 * <p>
 * <strong>For example:</strong>
 * <p>
 * <code>public void myObserver(@Observes {@link PreShutdown} event)<br/>
 * {<br/>
 *    // do something<br/>
 * }<br/>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class PreShutdown
{
   private final Shutdown.Status status;

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
