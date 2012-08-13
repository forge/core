/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * An aliasing wrapper for {@link MorePlugin}
 * 
 * @author Mike Brock .
 */
@Alias("less")
@Topic("Shell Environment")
public class LessPlugin implements Plugin
{
   private final MorePlugin morePlugin;

   @Inject
   public LessPlugin(@Alias("more") MorePlugin morePlugin)
   {
      this.morePlugin = morePlugin;
   }

   @DefaultCommand
   public void run(@PipeIn InputStream pipeIn,
            final Resource<?> file,
            final PipeOut pipeOut)
            throws IOException

   {
      morePlugin.run(pipeIn, file, true, pipeOut);
   }
}
