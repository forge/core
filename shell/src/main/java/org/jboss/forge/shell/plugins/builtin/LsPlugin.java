/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import static org.jboss.forge.shell.util.GeneralUtils.printOutColumns;
import static org.jboss.forge.shell.util.GeneralUtils.printOutTables;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
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
import org.jboss.forge.shell.util.FormatCallback;
import org.jboss.forge.shell.util.GeneralUtils;

/**
 * Lists directory contents for filesystem based directories. This is a simplified version of the UNIX 'ls' command and
 * currently supports the - and -a flags, as in unix.
 * 
 * @author Mike Brock
 */
@Alias("ls")
@Topic("File & Resources")
@RequiresResource(DirectoryResource.class)
@Help("Prints the contents current directory.")
public class LsPlugin implements Plugin
{
   private final Shell shell;

   private static final long yearMarker;
   private static final SimpleDateFormat dateFormatOld = new SimpleDateFormat("MMM d yyyy");
   private static final SimpleDateFormat dateFormatRecent = new SimpleDateFormat("MMM d HH:mm");

   static
   {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(System.currentTimeMillis());
      c.set(Calendar.MONTH, 0);
      c.set(Calendar.DAY_OF_MONTH, 0);
      c.set(Calendar.HOUR, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);

      yearMarker = c.getTimeInMillis();
   }

   @Inject
   public LsPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(
            @Option(description = "path", defaultValue = ".") Resource<?>[] paths,
            @Option(flagOnly = true, name = "all", shortName = "a", required = false) final boolean showAll,
            @Option(flagOnly = true, name = "list", shortName = "l", required = false) final boolean list,
            final PipeOut out)
   {

      Map<String, List<String>> sortMap = new TreeMap<String, List<String>>();
      List<String> listBuild = new LinkedList<String>();

      for (Resource<?> resource : paths)
      {
         List<Resource<?>> childResources;

         /**
          * Check to see if the way this resource was resolved was by a wildcard, in which case we don't expand into
          * it's children. Otherwise, if it's fully qualified we recurse into that directory and list all those files.
          */
         if (!resource.isFlagSet(ResourceFlag.AmbiguouslyQualified) && resource.isFlagSet(ResourceFlag.Node))
         {
            childResources = resource.listResources();
         }
         else
         {
            if (resource.exists())
            {
               childResources = Collections.<Resource<?>> singletonList(resource);
            }
            else
            {
               return;
            }
         }

         String el;
         File file;

         if (list)
         {
            /**
             * List-view implementation.
             */
            int fileCount = 0;
            boolean dir;
            List<String> subList;
            for (Resource<?> r : childResources)
            {
               sortMap.put(el = r.getName(), subList = new ArrayList<String>());
               file = (File) r.getUnderlyingResourceObject();

               if (dir = (r instanceof DirectoryResource))
               {
                  el += "/";
               }

               if (showAll || !el.startsWith("."))
               {
                  StringBuilder permissions = new StringBuilder(dir ? "d" : "-")
                           .append(file.canRead() ? 'r' : '-')
                           .append(file.canWrite() ? 'w' : '-')
                           .append(file.canExecute() ? 'x' : '-')
                           .append("------");

                  subList.add(permissions.toString());
                  subList.add("owner"); // not supported
                  subList.add(" users "); // not supported
                  subList.add(String.valueOf(file.length()));
                  subList.addAll(Arrays.asList(getDateString(file.lastModified())));
                  subList.add(el);

                  if (!dir)
                  {
                     fileCount++;
                  }
               }
            }

            for (List<String> sublist : sortMap.values())
            {
               listBuild.addAll(sublist);
            }

            out.println("total " + fileCount);
         }
         else
         {
            for (Resource<?> r : childResources)
            {
               el = r.getName();

               if (r instanceof DirectoryResource)
               {
                  el += "/";
               }

               if (showAll || !el.startsWith("."))
               {
                  listBuild.add(el);
               }
            }
         }
      }

      /**
       * print the results.
       */

      if (list)
      {
         FormatCallback formatCallback = new FormatCallback()
         {
            @Override
            public String format(int column, String value)
            {
               if ((column == 7) && value.endsWith("/"))
               {
                  return shell.renderColor(ShellColor.BLUE, value);
               }
               else
               {
                  return value;
               }
            }
         };

         try
         {
            shell.bufferingMode();
            printOutTables(
                     listBuild,
                     new boolean[] { false, false, false, true, false, false, true, false },
                     out,
                     formatCallback);
         }
         finally
         {
            shell.directWriteMode();
         }
      }
      else
      {
         FormatCallback formatCallback = new FormatCallback()
         {
            @Override
            public String format(int column, String value)
            {
               if (value.endsWith("/"))
               {
                  return out.renderColor(ShellColor.BLUE, value);
               }
               else
               {
                  return value;
               }
            }
         };
         try
         {
            shell.bufferingMode();
            if (out.isPiped())
            {
               GeneralUtils.OutputAttributes attr = new GeneralUtils.OutputAttributes(120, 1);
               printOutColumns(listBuild, ShellColor.NONE, out, attr, null, false);
            }
            else
            {
               printOutColumns(listBuild, out, shell, formatCallback, false);
            }
         }
         finally
         {
            shell.directWriteMode();
         }
      }
   }

   private static String[] getDateString(long time)
   {
      if (time < yearMarker)
      {
         return dateFormatOld.format(new Date(time)).split(" ");
      }
      else
      {
         return dateFormatRecent.format(new Date(time)).split(" ");
      }
   }
}
