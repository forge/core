/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import static org.jboss.forge.shell.util.GeneralUtils.printOutColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.constraint.ConstraintEnforcer;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.FormatCallback;
import org.jboss.forge.shell.util.GeneralUtils;

/**
 * @author Mike Brock
 */
@Alias("list-commands")
@Topic("Shell Environment")
@Help("Lists executable forge commands")
public class ListCommandsPlugin implements Plugin
{
   final PluginRegistry registry;
   final Shell shell;

   @Inject
   public ListCommandsPlugin(final PluginRegistry registry, final Shell shell)
   {
      this.registry = registry;
      this.shell = shell;
   }

   @SuppressWarnings("rawtypes")
   @DefaultCommand
   public void listCommands(
            @Option(name = "all", shortName = "a", flagOnly = true) final boolean showAll,
            final PipeOut pipeOut
            )
   {
      List<String> listData;
      Map<String, List<String>> listGroups = new TreeMap<String, List<String>>();

      Class<? extends Resource> currResource = shell.getCurrentResource().getClass();

      for (List<PluginMetadata> lpm : registry.getPlugins().values())
      {
         for (PluginMetadata pluginMetadata : lpm)
         {
            ConstraintEnforcer enforcer = new ConstraintEnforcer();
            if (showAll || enforcer.isAvailable(shell.getCurrentProject(), pluginMetadata))
            {
               if (!listGroups.containsKey(pluginMetadata.getTopic()))
               {
                  listGroups.put(pluginMetadata.getTopic(), listData = new ArrayList<String>());
               }
               else
               {
                  listData = listGroups.get(pluginMetadata.getTopic());
               }

               for (CommandMetadata commandMetadata : pluginMetadata.getAllCommands())
               {
                  String name = render(showAll, currResource, commandMetadata);

                  /**
                   * Deal with overloaded plugins.
                   */
                  if (name.endsWith("*"))
                  {
                     listData.remove(name.substring(0, name.length() - 1));
                  }
                  listData.remove(name);

                  if (!"".equals(name))
                  {
                     listData.add(name);
                  }
               }

               if (!listGroups.containsKey(pluginMetadata.getTopic()))
               {
                  listGroups.put(pluginMetadata.getTopic(), listData);
               }
            }

         }
      }

      GeneralUtils.OutputAttributes attr = null;

      if (pipeOut.isPiped())
      {
         attr = new GeneralUtils.OutputAttributes(120, 1);
      }
      else
      {
         for (Map.Entry<String, List<String>> entry : listGroups.entrySet())
         {
            attr = GeneralUtils.calculateOutputAttributs(entry.getValue(), shell, attr);
         }
      }

      FormatCallback formatCallback = new FormatCallback()
      {
         @Override
         public String format(final int column, final String value)
         {
            return value.endsWith("*") ? pipeOut.renderColor(ShellColor.BOLD, value) : value;
         }
      };

      for (Map.Entry<String, List<String>> entry : listGroups.entrySet())
      {
         if (!pipeOut.isPiped())
         {
            pipeOut.println();
            pipeOut.println(ShellColor.RED, "[" + entry.getKey().toUpperCase() + "]");
         }

         printOutColumns(entry.getValue(), ShellColor.NONE, pipeOut, attr, formatCallback, true);
      }

      if (!pipeOut.isPiped())
      {
         pipeOut.println();
         if (showAll)
         {
            pipeOut.println("(* = command accessible from current context)");
         }
         else
         {
            pipeOut.println("(only commands in relevant scope displayed. use --all to see all commands.)");
         }
      }
   }

   @SuppressWarnings("rawtypes")
   private static String render(final boolean showAll, final Class<? extends Resource> currResource,
            final CommandMetadata cmdMeta)
   {
      boolean contextual = cmdMeta.usableWithResource(currResource);

      if (showAll)
      {
         if (cmdMeta.isDefault())
         {
            return (cmdMeta.getName() + (contextual ? "*" : ""));
         }
         else
         {
            return (cmdMeta.getParent().getName() + " " + cmdMeta.getName() + (contextual ? "*" : ""));
         }
      }
      else if (contextual)
      {
         if (cmdMeta.isDefault())
         {
            return (cmdMeta.getName() + "*");
         }
         else
         {
            return (cmdMeta.getParent().getName() + " " + cmdMeta.getName() + "*");
         }
      }

      return "";
   }
}
