/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.WriteableResource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Lists;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
public class CopyCommand extends AbstractShellCommand
{

   @Inject
   ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(label = "Arguments", required = true, type = InputType.FILE_PICKER)
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      Iterable<String> value = arguments.getValue();
      if (value != null)
      {
         if (Lists.toList(value).size() != 2)
         {
            validator.addValidationError(arguments, "Only two arguments are accepted");
         }
      }
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), CopyCommand.class).name("cp")
               .description("Copy a file or directory");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UISelection<Resource<?>> initialSelection = context.getUIContext().getInitialSelection();
      Resource<?> directory = initialSelection.get();
      Iterator<String> argIterator = arguments.getValue().iterator();
      Resource<?> sourceResource = resolveFirstResource(directory, argIterator.next());
      String targetResource = argIterator.next();
      if (isDirectory(sourceResource))
      {
         copyRecursively(sourceResource, directory, targetResource);
      }
      else if (isFile(sourceResource))
      {
         copy(sourceResource, directory, targetResource);
      }
      else
      {
         throw new RuntimeException("cannot copy resource type: " + sourceResource.getClass().getSimpleName());
      }
      return Results.success();
   }

   private void copy(final Resource<?> source, Resource<?> directory, final String target)
   {
      Resource<?> targetResource = resolveFirstResource(directory, target);
      if (targetResource.exists())
      {
         if (isDirectory(targetResource))
         {
            targetResource = targetResource.getChild(source.getName());
         }
      }
      ((WriteableResource) targetResource).setContents(source.getResourceInputStream());
   }

   private void copyRecursively(final Resource<?> source, Resource<?> directory, final String target)
   {

      Resource<?> targetResource = resolveFirstResource(directory, target);
      Resource<?> newTargetDir = null;

      if (isDirectory(source))
      {
         if (!targetResource.exists())
         {
            newTargetDir = ((DirectoryResource) targetResource.getParent()).getOrCreateChildDirectory(targetResource
                     .getName());
         }
         else
         {
            newTargetDir = ((DirectoryResource) targetResource).getOrCreateChildDirectory(source.getName());
         }
         for (Resource<?> resource : source.listResources())
         {
            copyRecursively(resource, directory, newTargetDir.getFullyQualifiedName());
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
            ((WriteableResource) child).setContents(source.getResourceInputStream());
         }
         newTargetDir = targetResource;
      }
   }

   private Resource<?> resolveFirstResource(Resource<?> resource, final String target)
   {
      List<Resource<?>> results = resource.resolveChildren(target);
      if (results.size() > 1)
      {
         throw new RuntimeException("ambiguous target file name: " + target);
      }
      else if (results.isEmpty())
      {
         throw new RuntimeException("no resources found under path: " + target);
      }
      else
      {
         return results.get(0);
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
