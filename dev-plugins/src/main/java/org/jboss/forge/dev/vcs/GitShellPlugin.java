/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.vcs;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.NativeSystemCall;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock .
 */
@Alias("git")
@Topic("Version Control")
public class GitShellPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public GitShellPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(final PipeOut out, final String... parms) throws IOException
   {
      NativeSystemCall.execFromPath("git", parms, out, shell.getCurrentDirectory());
   }

}
