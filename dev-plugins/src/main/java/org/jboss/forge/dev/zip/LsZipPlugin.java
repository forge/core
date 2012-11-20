/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.zip;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.ZipEntryResource;
import org.jboss.forge.resources.ZipResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Current;
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
@RequiresResource({ ZipResource.class, ZipEntryResource.class })
@Help("List content of zip archive.")
public class LsZipPlugin implements Plugin
{
   @Inject
   private Shell shell;

   @Inject
   @Current
   private Resource<?> currentResource;

   @DefaultCommand
   public void run(
            @Option final String namePattern,
            @Option(name = "all", shortName = "a", flagOnly = true) final boolean recursively,
            final PipeOut out)
   {
      try
      {
         ResourceFilter resourceFilter = null;
         // Prepare filter option!
         if (namePattern != null && !namePattern.isEmpty())
         {
            resourceFilter = new NameRegExFilter(namePattern);
         }

         List<Resource<?>> resources = list(resourceFilter, recursively);

         for (Resource<?> resource : resources)
         {
            String name = resource.getName();
            if (recursively && resource instanceof ZipEntryResource)
            {
               name = resource.getFullyQualifiedName();
            }

            if (resource.isFlagSet(ResourceFlag.Node))
            {
               out.println(ShellColor.BLUE, name + "/");
            }
            else
            {
               out.println(name);
            }
         }
      }
      finally
      {
         shell.directWriteMode();
      }
   }

   protected List<Resource<?>> list(final ResourceFilter resourceFilter, final boolean recursively)
   {
      if (currentResource instanceof ZipResource)
      {
         return listZip((ZipResource) currentResource, resourceFilter, recursively);
      }
      else if (currentResource instanceof ZipEntryResource)
      {
         return listEntry((ZipEntryResource) currentResource, resourceFilter, recursively);
      }
      else
      {
         return listResource(currentResource, resourceFilter);
      }
   }

   protected List<Resource<?>> listZip(ZipResource zip, final ResourceFilter resourceFilter, final boolean recursively)
   {
      return zip.listResources(resourceFilter, recursively);
   }

   protected List<Resource<?>> listEntry(final ZipEntryResource entry, final ResourceFilter resourceFilter,
            final boolean recursively)
   {
      return entry.listResources(resourceFilter, recursively);
   }

   protected List<Resource<?>> listResource(Resource<?> resource, final ResourceFilter resourceFilter)
   {
      if (resource.isFlagSet(ResourceFlag.Node))
      {
         if (resourceFilter != null)
         {
            return resource.listResources(resourceFilter);
         }
         else
         {
            return resource.listResources();
         }
      }
      return Collections.emptyList();
   }

   private static class NameRegExFilter implements ResourceFilter
   {
      private Pattern pattern;

      public NameRegExFilter(String namePattern)
      {
         String regex = pathspecToRegEx(namePattern);
         try
         {
            pattern = Pattern.compile(regex);
         }
         catch (Exception e)
         {
            // Regex might be incomplete, trying again quoted
            pattern = Pattern.compile(Pattern.quote(namePattern));
         }
      }

      @Override
      public boolean accept(Resource<?> resource)
      {
         return pattern.matcher(resource.getName()).find();
      }

      public static String pathspecToRegEx(final String pathSpec)
      {
         StringBuilder sb = new StringBuilder("^");
         char c;
         for (int i = 0; i < pathSpec.length(); i++)
         {
            switch (c = pathSpec.charAt(i))
            {
            case '.':
               sb.append("\\.");
               break;
            case '*':
               sb.append(".*");
               break;
            case '?':
               sb.append(".");
               break;
            default:
               sb.append(c);
            }
         }
         return sb.append("$").toString();
      }
   }
}
