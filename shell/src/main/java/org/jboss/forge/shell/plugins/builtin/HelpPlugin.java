/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.GeneralUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("help")
@Topic("Shell Environment")
@Help("Displays help text for specified plugins & commands.")
public class HelpPlugin implements Plugin
{
   private static final String NO_HELP = "no help text available";

   private final PluginRegistry registry;
   private final Shell shell;

   @Inject
   public HelpPlugin(final Shell shell, final PluginRegistry registry)
   {
      this.shell = shell;
      this.registry = registry;
   }

   @DefaultCommand
   public void help(@Option(help = "The plugin name", description = "plugin name...") final String plugin,
            @Option(help = "The command name", description = "command name...") final String command,
            @Option(name = "all", shortName = "a", help = "show all...") final boolean all,
            final PipeOut out)
   {

      if (Strings.isNullOrEmpty(plugin) && Strings.isNullOrEmpty(command))
      {
         printGeneralHelp(out);
      }
      else
      {
         Map<String, List<PluginMetadata>> plugins = registry.getPlugins();

         List<PluginMetadata> list = plugins.get(plugin);

         if ((list == null) || list.isEmpty())
         {
            throw new RuntimeException("No such command [" + plugin + "]");
         }

         if (Strings.isNullOrEmpty(command))
         {
            printAllMessage(all, out, plugin, list);

            if (all)
            {
               for (PluginMetadata p : list)
               {
                  printPlugin(out, p, all);
               }
            }
            else
            {
               PluginMetadata p = registry.getPluginMetadataForScopeAndConstraints(plugin, shell);
               if (p == null)
               {
                  p = list.get(0);
               }
               printPlugin(out, p, all);
            }
         }

         if (!Strings.isNullOrEmpty(command))
         {
            PluginMetadata p = registry.getPluginMetadataForScopeAndConstraints(plugin, shell);
            if (p == null)
            {
               p = list.get(0);
            }

            out.println();
            if (p.hasCommand(command, shell))
            {
               CommandMetadata c = p.getCommand(command);
               out.print(ShellColor.BOLD, "[" + p.getName() + " " + c.getName() + "] ");
               out.println("- "
                        + (!Strings.isNullOrEmpty(c.getHelp()) ? c.getHelp() : out.renderColor(ShellColor.ITALIC,
                                 NO_HELP)));

               printOptions(out, c);
            }
            else
            {
               out.println("No such command [" + command + "] for the active Resource scope.");
            }
            out.println();
         }
      }

   }

   private void printAllMessage(final boolean all, final PipeOut out, final String tok, final List<PluginMetadata> list)
   {
      if (list.size() > 1)
      {
         if (all)
         {
            ShellMessages.info(out, "The plugin [" + tok
                     + "] is overloaded. Listing all candidates and their corresponding Resource scopes.");
         }
         else
         {
            ShellMessages
                     .info(out,
                              "The plugin ["
                                       + tok
                                       + "] is overloaded. Showing only the plugin for the first or active scope. Re-run with "
                                       + out.renderColor(ShellColor.BOLD, "'--all'") + " to display all scopes.");
         }
      }
   }

   private void printPlugin(final PipeOut out, final PluginMetadata p, final boolean all)
   {
      out.println();
      out.println(out.renderColor(ShellColor.BOLD, "[" + p.getName() + "]")
               + " - "
               + (Strings.isNullOrEmpty(p.getHelp()) ? out.renderColor(ShellColor.ITALIC, NO_HELP) : p
                        .getHelp()));
      if (!p.getResourceScopes().isEmpty() && all)
      {
         out.println(p.getResourceScopes().toString());
      }

      if (p.hasDefaultCommand())
      {
         CommandMetadata def = p.getDefaultCommand();
         if (def.hasOptions())
            printOptions(out, def);
      }

      List<CommandMetadata> commands = p.getCommands();
      if (commands.size() > 1)
      {
         out.println();
         out.println(ShellColor.RED, "[COMMANDS]");
         List<String> commandNames = new ArrayList<String>();
         for (CommandMetadata c : commands)
         {
            if (!c.isDefault())
               commandNames.add(c.getName());
         }
         GeneralUtils.printOutColumns(commandNames, out, shell, true);
      }

      out.println();
   }

   private void printOptions(final PipeOut out, final CommandMetadata def)
   {
      List<OptionMetadata> options = def.getOptions();

      if (!options.isEmpty())
      {
         out.println();
         out.println(ShellColor.RED, "[OPTIONS]");

         int i = 1;
         for (OptionMetadata opt : options)
         {
            if (opt.isOrdered())
            {
               out.print(ShellColor.BOLD, "\t[" + (opt.isVarargs() ? "Args..." : "Arg #" + i) + "]");
               out.print(Strings.isNullOrEmpty(opt.getDescription()) ? " - " : " - " + opt.getDescription() + " - ");
               out.println((!Strings.isNullOrEmpty(opt.getHelp()) ? opt.getHelp() : out.renderColor(ShellColor.ITALIC,
                        NO_HELP)));
               i++;
            }
         }

         for (OptionMetadata opt : options)
         {
            if (opt.isNamed())
            {
               out.print(ShellColor.BOLD, "\t[--" + opt.getName());
               if (!Strings.isNullOrEmpty(opt.getShortName()))
               {
                  out.print(", " + out.renderColor(ShellColor.BOLD, "-" + opt.getShortName()));
               }
               out.print(ShellColor.BOLD, "]");
               out.print(Strings.isNullOrEmpty(opt.getDescription()) ? " - " : " - " + opt.getDescription() + " - ");
               out.println((!Strings.isNullOrEmpty(opt.getHelp()) ? opt.getHelp() : out.renderColor(ShellColor.ITALIC,
                        NO_HELP)));
            }
         }
      }
   }

   private void printGeneralHelp(final PipeOut out)
   {
      out.println("Welcome to " + out.renderColor(ShellColor.YELLOW, "JBoss Forge") + ", a next-generation " +
               "interactive Shell and project-generation tool. If you find yourself lost, or uncertain how " +
               "to complete an operation, you may press the " +
               out.renderColor(ShellColor.BOLD, "<TAB>") + " key for command-completion, or " +
               out.renderColor(ShellColor.BOLD, "<TAB><TAB>") + " for hints while typing a command.");

      out.println();
      out.println("Type " + out.renderColor(ShellColor.BOLD, "'list-commands'") + " for a list of available " +
               "commands in the current Resource context.");

      out.println();
      Project currentProject = shell.getCurrentProject();
      if (currentProject != null)
      {
         out.println("Currently operating on the Project located at ["
                  + currentProject.getProjectRoot().getFullyQualifiedName() + "]");
      }
      else
      {
         out.println("You are not working on a project. Type " + out.renderColor(ShellColor.BOLD, "'help new-project'")
                  + " to get started.");
      }
   }

}
