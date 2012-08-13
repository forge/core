/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.i18n;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.resources.EntryResource;
import org.jboss.forge.resources.PropertiesFileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.Wait;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 */
@Alias("ls")
@RequiresResource(PropertiesFileResource.class)
@Topic("File & Resources")
@Help("Prints the contents of the current Properties file")
public class LsBundlePlugin implements Plugin
{
   @Inject
   private Wait wait;

   @SuppressWarnings("unchecked")
   @DefaultCommand
   public void run(
            @Option(description = "path", defaultValue = ".") final Resource<?>[] paths,
            final PipeOut out) throws FileNotFoundException
   {
      try
      {
         wait.start("Listing Resource Bundle Content ...");
         out.println();
         out.println();
         for (Resource<?> path : paths)
         {
            if (path instanceof PropertiesFileResource)
            {
               PropertiesFileResource propResource = (PropertiesFileResource) path;
               for (Resource<?> resource : propResource.listResources())
               {
                  EntryResource<String, String> entryResource = (EntryResource<String, String>) resource;
                  out.print(ShellColor.BOLD, entryResource.getKey() + ": ");
                  out.println(entryResource.getValue());
               }
               out.println();
            }
         }
      }
      finally
      {
         wait.stop();
      }
   }
}