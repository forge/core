/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.IOException;
import java.util.List;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("find")
@Topic("File & Resources")
@Help("Lists all resources recursively from the given resource.")
public class FindPlugin implements Plugin
{
   @DefaultCommand
   public void run(
            @Option(description = "The starting resource to be listed",
                     defaultValue = ".") final Resource<?> r,
            PipeOut out)
            throws IOException
   {
      listResources(out, r);
   }

   private void listResources(PipeOut out, Resource<?> r)
   {
      List<Resource<?>> list = r.listResources();
      out.println(r.getFullyQualifiedName());
      if (list != null && !list.isEmpty())
      {
         for (Resource<?> resource : list)
         {
            listResources(out, resource);
         }
      }
   }
}
