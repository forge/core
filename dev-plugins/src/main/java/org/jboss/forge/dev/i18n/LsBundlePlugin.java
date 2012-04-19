/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.dev.i18n;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.resources.PropertiesFileResource;
import org.jboss.forge.resources.Resource;
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
                  out.println(resource.getName());
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