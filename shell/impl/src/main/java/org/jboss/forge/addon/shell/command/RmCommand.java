/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Implementation of the "rm" command
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RmCommand extends AbstractShellCommand
{
   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER, required = true)
   private UIInputMany<String> arguments;

   @Inject
   @WithAttributes(label = "force", shortName = 'f', description = "ignore nonexistent files and arguments, never prompt", type = InputType.CHECKBOX, defaultValue = "false")
   private UIInput<Boolean> force;

   @Inject
   @WithAttributes(label = "recursive", shortName = 'r', description = "remove directories and their contents recursively", type = InputType.CHECKBOX, defaultValue = "false")
   private UIInput<Boolean> recursive;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("rm")
               .description("Remove (unlink) the FILE(s).");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments).add(force).add(recursive);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Resource<?> currentResource = (Resource<?>) context.getUIContext().getInitialSelection().get();
      for (String file : arguments.getValue())
      {
         List<Resource<?>> resources = currentResource.resolveChildren(file);
         for (Resource<?> resource : resources)
         {
            if (!resource.exists())
            {
               return Results.fail(file + ": no such file or directory");
            }
         }
      }

      boolean forceOption = force.getValue();
      boolean recurse = recursive.getValue();
      UIPrompt prompt = context.getPrompt();
      UIOutput output = context.getUIContext().getProvider().getOutput();
      for (String file : arguments.getValue())
      {
         List<Resource<?>> resources = currentResource.resolveChildren(file);
         for (Resource<?> resource : resources)
         {
            if ((resource instanceof DirectoryResource))
            {
               if (!recurse)
               {
                  output.err().println(
                           "rm: cannot remove '" + resource.getName()
                                    + "': Is a directory ");
               }
               else if (!resource.listResources().isEmpty() && !forceOption)
               {
                  output.err().println(
                           "rm: directory '" + resource.getName()
                                    + "' not empty and cannot be deleted without '--force' '-f' option.");
               }
               else if (forceOption || prompt.promptBoolean("Delete '" + resource.getFullyQualifiedName() + "'?"))
               {
                  if (!resource.delete(recurse))
                  {
                     output.err().println("rm: cannot remove ‘" + resource.getFullyQualifiedName()
                              + "’: Error occurred during deletion");
                  }
               }
            }
            else
            {
               if (!resource.delete(recurse))
               {
                  output.err().println("rm: cannot remove ‘" + resource.getFullyQualifiedName()
                           + "’: Error occurred during deletion");
               }
            }

         }
      }
      while (!currentResource.exists())
      {
         currentResource = currentResource.getParent();
      }
      context.getUIContext().setSelection(currentResource);
      return Results.success();
   }
}
