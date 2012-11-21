/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Dan Allen
 */
@Alias("ls")
@RequiresResource(GitIgnoreResource.class)
@Topic("Files & Resources")
@Help("Prints the contents of the current gitignore file")
public class LsGitIgnore implements Plugin
{

   @DefaultCommand
   public void ls(@Option(description = "path", defaultValue = ".") final Resource<?> resource, PipeOut out)
   {
      for (String pattern : ((GitIgnoreResource) resource).getPatterns())
      {
         out.println(pattern);
      }
   }

}
