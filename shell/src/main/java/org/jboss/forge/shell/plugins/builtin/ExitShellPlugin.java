/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.shell.events.Shutdown;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * Implements a {@link Plugin} that fires the forge {@link ShutdownStatus#NORMAL} event.
 * 
 * 
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("exit")
@Topic("Shell Environment")
@Help("Exits the forge.")
public class ExitShellPlugin implements Plugin
{
   @Inject
   private Event<Shutdown> shutdown;

   @DefaultCommand
   public void exit()
   {
      shutdown.fire(new Shutdown(Shutdown.Status.NORMAL));
   }
}
