/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;

/**
 * Create the DIRECTORY(ies), if they do not already exist.
 * 
 * @author Mike Brock
 * @author George Gastaldi
 */
@Alias("mkdir")
@Topic("File & Resources")
@RequiresResource(DirectoryResource.class)
@Help("Create a new directory")
public class MkdirPlugin implements Plugin
{
   @Inject
   @Current
   private DirectoryResource currentDir;

   @DefaultCommand
   public void mkdir(
            @Option(help = "name of directory to be created", required = true) final String name)
   {
      FileResource<?> newResource = (FileResource<?>) currentDir.getChild(name);
      if (newResource.exists())
      {
         throw new RuntimeException(String.format("cannot create directory '%s': File exists", name));
      }
      else if (!newResource.mkdirs())
      {
         throw new RuntimeException("failed to create directory: " + name);
      }
   }
}
