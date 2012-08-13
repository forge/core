/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.bus.EventBus;
import org.jboss.forge.shell.events.CommandExecuted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EventBusInvoker
{
   @Inject
   private EventBus bus;

   @SuppressWarnings("unused")
   private void fire(@Observes final CommandExecuted event)
   {
      bus.fireAll();
   }
}
