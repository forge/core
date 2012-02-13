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
package org.jboss.forge.shell.plugins.builtin;

import static org.jboss.forge.shell.util.GeneralUtils.printOutColumns;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.enumtype.EnumTypeResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.GeneralUtils;
import org.jboss.forge.shell.util.JavaColorizer;

/**
 * @author Ricardo Martinelli
 */
@Alias("ls")
@RequiresResource({ EnumTypeResource.class })
@Topic("File & Resources")
@Help("Prints the contents current Enum Type file")
public class LsEnumTypePlugin implements Plugin
{
   @Inject
   private Shell shell;

   @DefaultCommand
   public void run(
            @Option(description = "path", defaultValue = ".") final Resource<?>[] paths,
            @Option(flagOnly = true, name = "all", shortName = "a", required = false) final boolean showAll,
            @Option(flagOnly = true, name = "list", shortName = "l", required = false) final boolean list,
            final PipeOut out) throws FileNotFoundException
   {
      for (Resource<?> resource : paths)
      {
         if (resource instanceof EnumTypeResource)
         {
            if (showAll)
            {
               out.print(JavaColorizer.format(out, ((EnumTypeResource) resource).getEnumSource().toString()));
            }
            else
            {
               EnumTypeResource enumTypeResource = (EnumTypeResource) resource;
               List<String> output = new ArrayList<String>();

               if (!out.isPiped())
               {
                  out.println();
                  out.println(ShellColor.RED, "[fields]");
               }

               List<Resource<?>> members = enumTypeResource.listResources();
               for (Resource<?> member : members)
               {
                  String entry = member.getName();
                  output.add(entry);
               }

               if (out.isPiped())
               {
                  GeneralUtils.OutputAttributes attr = new GeneralUtils.OutputAttributes(120, 1);
                  printOutColumns(output, ShellColor.NONE, out, attr, null, false);
               }
               else
               {
                  GeneralUtils.printOutColumns(output, out, shell, true);
                  out.println();
               }
            }
         }
      }
   }
}