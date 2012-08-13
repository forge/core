/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.shell.events.ReinitializeEnvironment;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("reset")
@Topic("Shell Environment")
@Help("Reset the shell and reload default configs.")
public class ResetPlugin implements Plugin
{
   private final Event<ReinitializeEnvironment> reinitializeEvent;

   @Inject
   public ResetPlugin(final Event<ReinitializeEnvironment> reinitializeEvent)
   {
      this.reinitializeEvent = reinitializeEvent;
   }

   @DefaultCommand
   public void reset()
   {
      reinitializeEvent.fire(new ReinitializeEnvironment());
   }
}
