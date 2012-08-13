/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Mike Brock .
 */
@Alias("wait")
@Topic("Shell Environment")
@Help("Wait for ENTER.")
public class WaitPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public WaitPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void waitCommand()
   {
      shell.prompt("Press <ENTER> to continue...");
   }
}
