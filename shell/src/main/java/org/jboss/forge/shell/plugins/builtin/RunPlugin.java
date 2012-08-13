/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Mike Brock .
 */
@Alias("run")
@Topic("Shell Environment")
public class RunPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public RunPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@Option(description = "file...", required = true) final Resource<?> r, final String... args)
            throws Exception
   {

      if (r instanceof FileResource)
      {
         try
         {
            shell.execute(((FileResource<?>) r).getUnderlyingResourceObject(), args);
         }
         catch (IOException e)
         {
            throw new RuntimeException("error executing script from file: " + r.getName());
         }
      }
      else
      {
         throw new RuntimeException("resource type not an executable script: " + r.getClass().getName());
      }

   }
}
