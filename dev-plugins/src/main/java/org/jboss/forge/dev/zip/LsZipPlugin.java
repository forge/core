package org.jboss.forge.dev.zip;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.ZipEntryResource;
import org.jboss.forge.resources.ZipResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;

/**
 * This is a plugin that list the content of zip archives, like *.zip, *.jar, *.war, *.ear.
 * 
 * @author Adolfo Junior
 */
@Alias("ls")
@Topic("File & Resources")
@RequiresResource(ZipResource.class)
@Help("List content of zip archive.")
public class LsZipPlugin implements Plugin
{
   @Inject
   private Shell shell;

   @DefaultCommand
   public void run(@Option(description = "path", defaultValue = ".") Resource<?>[] resources, final PipeOut out)
   {
      try
      {
         for (Resource<?> resource : resources)
         {
            List<Resource<?>> children;
            // List zip content or node children.
            if (resource instanceof ZipResource || resource.isFlagSet(ResourceFlag.Node))
            {
               children = resource.listResources();
            }
            else
            {
               children = Collections.<Resource<?>> singletonList(resource);
            }

            print(children, out);
         }
      }
      finally
      {
         shell.directWriteMode();
      }
   }

   protected void print(final List<Resource<?>> resources, final PipeOut out)
   {
      for (Resource<?> resource : resources)
      {
         if (resource instanceof ZipEntryResource)
         {
            out.println(ShellColor.YELLOW, resource.getName());
         }
         else if (resource.isFlagSet(ResourceFlag.Node))
         {
            out.println(ShellColor.BLUE, resource.getName());
         }
         else
         {
            out.println(resource.getName());
         }
      }
   }
}
