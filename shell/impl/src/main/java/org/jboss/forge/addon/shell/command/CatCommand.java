package org.jboss.forge.addon.shell.command;

import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.addon.shell.Shell;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CatCommand extends AbstractShellCommand
{
   @Inject
   private ResourceFactory resourceFactory;

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
      Shell shell = (Shell) context.getUIContext().getProvider();
      Resource<?> currentResource = shell.getCurrentResource();
      Iterator<String> it = arguments.getValue() == null ? Collections.<String> emptyList().iterator() : arguments
               .getValue().iterator();

      Result result = Results.success();
      UIOutput output = shell.getOutput();
      while (it.hasNext())
      {
         final Resource<?> resource = it.hasNext() ?
                  (new ResourcePathResolver(resourceFactory, currentResource, it.next()).resolve().get(0))
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
                  highlighter.byFileName(resource.getName(), resource.getContents(), output.out());
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
