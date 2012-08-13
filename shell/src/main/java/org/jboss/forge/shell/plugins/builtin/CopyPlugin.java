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
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PathspecParser;

/**
 * 
 * Builtin copy plugin
 * 
 * @author tremes@redhat.com
 * 
 */
@Alias("cp")
@Topic("File & Resources")
@RequiresResource(DirectoryResource.class)
@Help("Copy a file or directory")
public class CopyPlugin implements Plugin
{

   private final ResourceFactory resourceFactory;

   @Inject
   public CopyPlugin(final ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @DefaultCommand
   public void rename(
            @Option(description = "source", required = true) final Resource<?> source,
            @Option(description = "target", required = true) final String target)
   {
      if (isDirectory(source))
      {
         Resource<?> directory = source.getParent();
         copyRecursively(source, directory, target);
      }
      else if (isFile(source))
      {
         Resource<?> directory = source.isFlagSet(ResourceFlag.Leaf) ? source.getParent() : source;
         copy(source, directory, target);
      }
      else
      {
         throw new RuntimeException("cannot copy resource type: " + source.getClass().getSimpleName());
      }
   }

   private void copy(final Resource<?> source, Resource<?> directory, final String target)
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
         }
         ((FileResource<?>) targetResource).setContents(source.getResourceInputStream());
      }
   }

   private void copyRecursively(final Resource<?> source, Resource<?> directory, final String target)
   {
      List<Resource<?>> results = new PathspecParser(resourceFactory, directory, target).resolve();

      if (results.size() > 1)
      {
         throw new RuntimeException("ambiguous target file name: " + target);
      }
      else
      {
         Resource<?> targetResource = results.get(0);
         List<Resource<?>> childs = source.listResources();
         Resource<?> newTargetDir = null;

         if (isDirectory(source))
         {

            if (!targetResource.exists())
            {

               newTargetDir = ((DirectoryResource) source.getParent()).getOrCreateChildDirectory(targetResource
                        .getName());

            }
            else
            {

               newTargetDir = ((DirectoryResource) targetResource).getOrCreateChildDirectory(source.getName());

            }

         }
         else if (isFile(source))
         {

            Resource<?> child = targetResource.getChild(source.getName());

            if (child == null)
            {

               ((DirectoryResource) targetResource).getOrCreateChildDirectory(source.getName()).setContents(
                        source.getResourceInputStream());

            }
            else
            {

               ((FileResource<?>) child).setContents(source.getResourceInputStream());

            }
            newTargetDir = (DirectoryResource) targetResource;
         }

         if (childs.size() > 0)
         {
            for (Resource<?> resource : childs)
            {
               copyRecursively(resource, directory, newTargetDir.getFullyQualifiedName());
            }
         }
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
