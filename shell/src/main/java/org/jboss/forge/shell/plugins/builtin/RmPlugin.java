/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Mike Brock
 */
@Alias("rm")
@Topic("File & Resources")
@Help("Removes a resource")
public class RmPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public RmPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void rm(
            @Option(name = "recursive", shortName = "r", help = "recursively delete resources", flagOnly = true) final boolean recursive,
            @Option(name = "force", shortName = "f", help = "do not prompt to confirm operations", flagOnly = true) final boolean force,
            @Option(description = "path", required = true) final Resource<?>[] paths)
   {
      for (Resource<?> resource : paths)
      {
         if (force || shell.promptBoolean("delete: " + resource.getName() + ": are you sure?"))
         {
            if (!resource.delete(recursive))
            {
               throw new RuntimeException("error deleting files.");
            }
         }
      }
   }
}
