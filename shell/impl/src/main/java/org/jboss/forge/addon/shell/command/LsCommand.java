package org.jboss.forge.addon.shell.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.aesh.comparators.PosixFileNameComparator;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.TerminalSize;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.parser.java.resources.JavaFieldResource;
import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
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
@SuppressWarnings("rawtypes")
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
         try
         {
            resourceList = new ResourcePathResolver(resourceFactory, currentResource, value).resolve();
         }
         catch (RuntimeException re)
         {
            if (re.getMessage() == null || !re.getMessage().contains("no such child"))
            {
               throw re;
            }
            else
            {
               return Results.fail(value + ": No such file or directory");
            }
         }
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
      if (resources == null)
      {
         return "";
      }

      TerminalSize terminalSize = shell.getConsole().getShell().getSize();

      List<FileResource> fileResources = new ArrayList<>();
      List<JavaFieldResource> fieldResources = new ArrayList<>();
      List<JavaMethodResource> methodResources = new ArrayList<>();
      List<Resource> otherResources = new ArrayList<>();

      for (Resource<?> resource : resources)
      {
         if (resource instanceof FileResource)
         {
            fileResources.add((FileResource) resource);
         }
         else if (resource instanceof JavaFieldResource)
         {
            fieldResources.add((JavaFieldResource) resource);
         }
         else if (resource instanceof JavaMethodResource)
         {
            methodResources.add((JavaMethodResource) resource);
         }
         else
         {
            otherResources.add(resource);
         }
      }

      StringBuilder sb = new StringBuilder();

      if (fileResources.size() > 0)
      {
         sb.append(getFileFormattedList(fileResources, terminalSize.getHeight(), terminalSize.getWidth()));
      }

      if (fieldResources.size() > 0)
      {
         sb.append(Config.getLineSeparator());
         sb.append(ShellUtil.colorizeLabel("[fields]"));
         sb.append(Config.getLineSeparator());
         sb.append(getJavaFieldFormattedList(fieldResources, terminalSize.getHeight(), terminalSize.getWidth()));
      }

      if (methodResources.size() > 0)
      {
         sb.append(Config.getLineSeparator());
         sb.append(ShellUtil.colorizeLabel("[methods]"));
         sb.append(Config.getLineSeparator());
         sb.append(getJavaMethodFormattedList(methodResources, terminalSize.getHeight(), terminalSize.getWidth()));
      }

      if (otherResources.size() > 0)
      {
         sb.append(getFormattedList(otherResources, terminalSize.getHeight(), terminalSize.getWidth()));
      }

      return sb.toString();
   }

   private String getFileFormattedList(List<FileResource> resources, int termHeight, int termWidth)
   {

      boolean showAll = all.getValue();
      List<TerminalString> display = new ArrayList<>();

      for (FileResource resource : resources)
      {
         if (!showAll && resource.getName().startsWith("."))
         {
            continue;
         }
         display.add(ShellUtil.colorizeResourceTerminal(resource));
      }

      Comparator<TerminalString> posixFileNameTerminalComparator = new Comparator<TerminalString>()
      {
         private PosixFileNameComparator posixFileNameComparator =
                  new PosixFileNameComparator();

         @Override
         public int compare(TerminalString o1, TerminalString o2)
         {
            return posixFileNameComparator.compare(o1.getCharacters(), o2.getCharacters());
         }
      };

      Collections.sort(display, posixFileNameTerminalComparator);

      return Parser.formatDisplayCompactListTerminalString(display, termWidth);
   }

   private String getJavaFieldFormattedList(List<JavaFieldResource> resources, int termHeight, int termWidth)
   {
      List<String> display = new ArrayList<>();

      for (JavaFieldResource resource : resources)
      {
         display.add(ShellUtil.colorizeJavaFieldResource(resource));
      }

      return Parser.formatDisplayList(display, termHeight, termWidth);
   }

   private String getJavaMethodFormattedList(List<JavaMethodResource> resources, int termHeight, int termWidth)
   {
      List<String> display = new ArrayList<>();

      for (JavaMethodResource resource : resources)
      {
         display.add(ShellUtil.colorizeJavaMethodResource(resource));
      }

      return Parser.formatDisplayList(display, termHeight, termWidth);
   }

   private String getFormattedList(List<Resource> resources, int termHeight, int termWidth)
   {
      List<String> display = new ArrayList<>();

      for (Resource resource : resources)
      {
         display.add(resource.getName());
      }

      return Parser.formatDisplayList(display, termHeight, termWidth);
   }
}
