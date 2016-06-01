/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.text.Highlighter;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Concatenate files and print in the standard output
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CatCommand extends AbstractShellCommand
{
   @Inject
   private Highlighter highlighter;

   @Inject
   @WithAttributes(shortName = 'c', label = "Color", description = "Enable color hightlight in output")
   private UIInput<Boolean> color;

   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER)
   private UIInputMany<String> arguments;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name("cat")
               .description(
                        "The cat utility reads files sequentially, writing them to the standard output.  "
                                 + "The file operands are processed in command-line order.");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
      builder.add(color);

      color.setDefaultValue(false);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Resource<?> currentResource = (Resource<?>) uiContext.getInitialSelection().get();
      Iterator<String> it = arguments.getValue() == null ? Collections.<String> emptyList().iterator() : arguments
               .getValue().iterator();

      Result result = Results.success();
      UIOutput output = uiContext.getProvider().getOutput();
      while (it.hasNext())
      {
         final Resource<?> resource = it.hasNext() ? (currentResource.resolveChildren(it.next()).get(0))
                  : currentResource;

         if (!resource.exists())
         {
            output.err().println("cat: " + resource.getName() + ": No such file or directory");
            result = Results.fail();
         }
         else
         {
            try
            {
               if (color.getValue())
               {
                  try
                  {
                     highlighter.byFileName(resource.getName(), resource.getContents(), output.out());
                     output.out().println();
                  }
                  catch (IllegalArgumentException iae)
                  {
                     output.warn(output.err(), "Error while rendering output in color: " + iae.getMessage());
                     output.out().println(resource.getContents());
                  }
               }
               else
               {
                  output.out().println(resource.getContents());
               }
            }
            catch (UnsupportedOperationException uoe)
            {
               output.err().println("cat: " + resource.getName() + ": " + uoe.getMessage());
               result = Results.fail();
            }
         }
      }
      return result;
   }
}
