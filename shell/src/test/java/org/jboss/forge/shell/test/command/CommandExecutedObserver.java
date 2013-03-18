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
import org.jboss.forge.shell.events.PreCommandExecution;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 *
 */
@Singleton
public class CommandExecutedObserver
{
   private CommandExecuted event;
   private PreCommandExecution preCommandExecutionEvent;
   private boolean veto;

   void handleCommand(@Observes final PreCommandExecution event)
   {
      this.preCommandExecutionEvent = event;
      if (veto)
      {
         event.veto();
      }
   }

   void handleCommand(@Observes final CommandExecuted event)
   {
      this.event = event;
   }

   public CommandExecuted getEvent()
   {
      return event;
   }

   public PreCommandExecution getPreCommandExecutionEvent()
   {
      return preCommandExecutionEvent;
   }

   public void setVeto(boolean veto)
   {
      this.veto = veto;
   }

   public void reset()
   {
      this.veto = false;
      this.event = null;
      this.preCommandExecutionEvent = null;
   }

}
