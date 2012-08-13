/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.shell.events.CommandExecuted;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * 
 */
@Singleton
public class CommandExecutedObserver
{
   private CommandExecuted event;

   void handleCommand(@Observes final CommandExecuted event)
   {
      this.event = event;
   }

   public CommandExecuted getEvent()
   {
      return event;
   }

}
