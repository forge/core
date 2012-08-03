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
