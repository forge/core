/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Implementation of the "mkdir" command
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MkdirCommand extends AbstractShellCommand
{
   @Inject
   ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER, required = true)
   private UIInputMany<String> arguments;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("mkdir")
               .description("Create a new directory.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Resource<?> currentResource = (Resource<?>) context.getUIContext().getInitialSelection().get();
      for (String path : arguments.getValue())
      {
         List<Resource<?>> resources = currentResource.resolveChildren(path);
         for (Resource<?> resource : resources)
         {
            if (resource.exists())
            {
               return Results.fail(path + ": Resource already exists.");
            }
            else
            {
               DirectoryResource directory = resourceFactory.create(DirectoryResource.class,
                        new File(resource.getFullyQualifiedName()));
               directory.mkdirs();
            }
         }
      }

      return Results.success();
   }

   @Override
   public boolean isEnabled(ShellContext context)
   {
      return super.isEnabled(context) && context.getInitialSelection().get() instanceof DirectoryResource;
   }
}
