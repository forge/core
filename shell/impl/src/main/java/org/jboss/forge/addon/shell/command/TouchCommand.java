/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
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
 * Implementation of the "touch" command
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class TouchCommand extends AbstractShellCommand
{
   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER, required = true)
   private UIInputMany<String> arguments;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("touch")
               .description("Create a new file or modify file timestamp.");
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
         if (resources.isEmpty())
         {
            return Results.fail(path + ": path could not be resolved");
         }
         for (Resource<?> resource : resources)
         {
            FileResource<?> file = resource.reify(FileResource.class);
            if (file != null)
            {
               if (file.exists())
               {
                  file.setLastModified(System.currentTimeMillis());
               }
               else if (!file.createNewFile())
               {
                  return Results.fail(path + ": file could not be created.");
               }
            }
         }
      }

      return Results.success();
   }
}
