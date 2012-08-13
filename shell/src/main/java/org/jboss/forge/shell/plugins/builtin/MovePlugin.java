/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PathspecParser;

/**
 * Implementation of UNIX-style "mv" (move) command for use within the Forge Shell.
 * 
 * @author Mike Brock
 */
@Alias("mv")
@Topic("File & Resources")
@RequiresResource(DirectoryResource.class)
@Help("Renames a file or directory")
public class MovePlugin implements Plugin
{
   private final ResourceFactory resourceFactory;

   @Inject
   public MovePlugin(final ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @DefaultCommand
   public void rename(
            @Option(description = "source", required = true) final Resource<?> source,
            @Option(description = "target", required = true) final String target,
            @Option(name = "force", shortName = "f", description = "force operation", flagOnly = true) final boolean force,
            final PipeOut out)
   {
      if (isDirectory(source))
      {
         Resource<?> directory = source.getParent();
         rename(source, directory, target, force, out);
      }
      else if (isFile(source))
      {
         Resource<?> directory = source.isFlagSet(ResourceFlag.Leaf) ? source.getParent() : source;
         rename(source, directory, target, force, out);
      }
      else
      {
         throw new RuntimeException("cannot rename resource type: " + source.getClass().getSimpleName());
      }
   }

   private void rename(final Resource<?> source, Resource<?> directory, final String target, final boolean force,
            final PipeOut out)
   {
      List<Resource<?>> results = new PathspecParser(resourceFactory, directory, target).resolve();

      if (results.size() > 1)
      {
         throw new RuntimeException("ambiguous target file name: " + target);
      }
      else
      {
         Resource<?> targetResource = results.get(0);

         if (targetResource.exists())
         {
            if (isDirectory(targetResource))
            {
               targetResource = targetResource.getChild(source.getName());
            }
            else if (force && (isFile(targetResource)))
            {
               ((FileResource<?>) targetResource).delete(false);
            }
            else
            {
               throw new RuntimeException("destination file exists: " + targetResource.getFullyQualifiedName());
            }
         }

         ((FileResource<?>) source).renameTo(targetResource.getFullyQualifiedName());
      }
   }

   private boolean isFile(Resource<?> source)
   {
      return source instanceof FileResource;
   }

   private boolean isDirectory(Resource<?> source)
   {
      return source instanceof DirectoryResource;
   }
}
