/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.NativeSystemCall;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
@Alias("edit")
@Topic("File & Resources")
@Help("Edit files with the default system editor")
public class EditPlugin implements Plugin
{
   @Inject
   public EditPlugin()
   {
   }

   @DefaultCommand
   public void run(@Option(description = "The files to edit", defaultValue = ".") final Resource<?>[] dirs)
            throws IOException
   {
      for (Resource<?> resource : dirs)
      {
         if (resource instanceof FileResource<?>)
         {
            Desktop dt = Desktop.getDesktop();
            try
            {
               dt.edit((File) resource.getUnderlyingResourceObject());
            }
            catch (UnsupportedOperationException e)
            {
               if (OSUtils.isLinux())
               {
                  NativeSystemCall.exec(true, "gedit", resource.getFullyQualifiedName());
               }
               else
                  throw e;
            }
         }
      }
   }
}
