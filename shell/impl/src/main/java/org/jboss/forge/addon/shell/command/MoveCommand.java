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
 * @author <a href="danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
public class MoveCommand extends AbstractShellCommand
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
      return Metadata.from(super.getMetadata(context), MoveCommand.class).name("mv")
               .description("Move a file or directory");
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      UISelection<Resource<?>> initialSelection = context.getUIContext().getInitialSelection();
      Resource<?> directory = initialSelection.get();
      Iterator<String> argIterator = arguments.getValue().iterator();
      FileResource<?> sourceResource = resolveFirstResource(directory, argIterator.next());
      FileResource<?> targetResource = resolveFirstResource(directory, argIterator.next());
      sourceResource.moveTo(targetResource);
      return Results.success();
   }

   private FileResource<?> resolveFirstResource(Resource<?> resource, final String target)
   {

      if (!isFile(resource) || !isDirectory(resource))
      {
         throw new RuntimeException(resource.getName() + " isn't a folder or file");
      }

      List<Resource<?>> results = resource.resolveChildren(target);
      if (results.size() > 1)
      {
         throw new RuntimeException("ambiguous target file name: " + target);
      }

      if (results.isEmpty())
      {
         throw new RuntimeException("no resources found under path: " + target);
      }

      return results.get(0).reify(FileResource.class);
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
