/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
@Alias("cd")
@Topic("File & Resources")
@Help("Change the current directory")
public class ChangeDirectoryPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public ChangeDirectoryPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@Option(description = "The new directory", defaultValue = "~") Resource<?> r)
   {
      if (r != null)
      {
         Project currentProject = shell.getCurrentProject();
         if (!r.exists())
         {
            if ("~".equals(r.getName()) && currentProject.exists())
            {
               r = currentProject.getProjectRoot();
            }
            else
               throw new RuntimeException("no such resource: " + r.toString());
         }

         String fullyQualifiedName = r.getFullyQualifiedName();
         String userHomePath = OSUtils.getUserHomePath();

         if (fullyQualifiedName.startsWith(userHomePath)
                  && "~".equals(r.getFullyQualifiedName().substring(userHomePath.length())))
         {
            r = currentProject.getProjectRoot();
         }

         shell.setCurrentResource(r);
      }
   }
}
