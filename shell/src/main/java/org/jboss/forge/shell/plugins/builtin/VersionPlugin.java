/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("version")
@Topic("Shell Environment")
@Help("Displays the current Forge version.")
public class VersionPlugin implements Plugin
{

   @Inject
   private ForgeEnvironment environment;

   @DefaultCommand
   public void show(final PipeOut out)
   {
      String version = environment.getRuntimeVersion();
      out.println("JBoss Forge, version [ " + version + " ] - JBoss, by Red Hat, Inc. [ http://jboss.org/forge ]");
   }
}
