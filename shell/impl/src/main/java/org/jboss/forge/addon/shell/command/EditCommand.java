/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EditCommand extends AbstractShellCommand
{
   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER)
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("edit")
               .description("Edit files with the default system editor");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Resource<?> currentResource = (Resource<?>) context.getUIContext().getInitialSelection().get();
      Iterable<String> value = arguments.getValue();
      Iterator<String> it = value == null ? Collections.<String> emptyIterator() : value.iterator();
      final Result result;
      if (it.hasNext())
      {
         String newPath = it.next();
         final List<Resource<?>> newResource = currentResource.resolveChildren(newPath);
         if (newResource.isEmpty() || !newResource.get(0).exists())
         {
            result = Results.fail(newPath + ": No such file or directory");
         }
         else
         {
            for (Resource<?> resource : newResource)
            {
               editResource(context, resource);
            }
            result = Results.success();
         }
      }
      else if (currentResource != null)
      {
         editResource(context, currentResource);
         result = Results.success();
      }
      else
      {
         result = Results.fail("Resource not found");
      }
      return result;
   }

   private void editResource(UIExecutionContext context, Resource<?> resource) throws IOException
   {
      UIDesktop desktop = context.getUIContext().getProvider().getDesktop();
      FileResource<?> fileResource = resource.reify(FileResource.class);
      if (fileResource != null && !fileResource.isDirectory())
      {
         desktop.edit(fileResource.getUnderlyingResourceObject());
      }
      else
      {
         UIOutput output = context.getUIContext().getProvider().getOutput();
         output.warn(output.err(), "Cannot edit [" + resource.getFullyQualifiedName() + "].");
      }
   }

}
