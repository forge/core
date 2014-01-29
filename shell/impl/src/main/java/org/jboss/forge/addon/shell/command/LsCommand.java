package org.jboss.forge.addon.shell.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.TerminalSize;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.util.ShellUtil;
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
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class LsCommand extends AbstractShellCommand
{

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(label = "Arguments", type = InputType.FILE_PICKER)
   private UIInputMany<String> arguments;

   @Inject
   @WithAttributes(label = "all", shortName = 'a', description = "do not ignore entries starting with .", type = InputType.CHECKBOX, defaultValue = "false")
   private UIInput<Boolean> all;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("ls").description("List files");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments).add(all);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Shell shell = (Shell) context.getUIContext().getProvider();
      Resource<?> currentResource = shell.getCurrentResource();
      Iterator<String> it = arguments.hasValue() ? arguments.getValue().iterator() : Collections
               .<String> emptyIterator();

      List<Resource<?>> resourceList;
      if (it.hasNext())
      {
         String value = it.next();
         boolean searching = (value.matches(".*(\\?|\\*)+.*"));
         resourceList = new ResourcePathResolver(resourceFactory, currentResource, value).resolve();
         if (!searching && !resourceList.isEmpty() && resourceList.get(0).exists())
         {
            resourceList = resourceList.get(0).listResources();
         }
      }
      else
      {
         resourceList = currentResource.listResources();
      }
      final Result result;
      if (!resourceList.isEmpty() && !resourceList.get(0).exists())
      {
         result = Results.fail(resourceList.get(0).getName() + ": No such file or directory");
      }
      else
      {
         UIOutput output = shell.getOutput();
         output.out().println(listMany(resourceList, shell));
         result = Results.success();
      }
      return result;
   }

   private String listMany(Iterable<Resource<?>> resources, Shell shell)
   {
      TerminalSize terminalSize = shell.getConsole().getShell().getSize();
      List<String> display = new ArrayList<>();
      boolean showAll = all.getValue();
      if (resources != null)
      {
         for (Resource<?> resource : resources)
         {
            String name;
            if (resource instanceof FileResource)
            {
               FileResource<?> fileResource = (FileResource<?>) resource;
               if (!showAll && fileResource.getName().startsWith("."))
               {
                  continue;
               }
               name = ShellUtil.colorizeResource(fileResource);
            }
            else
            {
               name = resource.getName();
            }
            display.add(name);
         }
      }
      return Parser.formatDisplayList(display, terminalSize.getHeight(), terminalSize.getWidth());
   }
}
