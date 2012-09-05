/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.event;


/**
 * Fired when the container begins its startup process.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Startup
{
   private boolean restart;

   public Startup()
   {
   }

   public boolean isRestart()
   {
      return restart;
   }
}
