/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

@Alias("about")
@Topic("Shell Environment")
@Help("Display information about this forge.")
public class AboutPlugin implements Plugin
{
   @Inject
   @Alias("forge")
   private ForgePlugin forge;

   @DefaultCommand
   public void run(PipeOut out)
   {
      forge.about(out);
   }
}
