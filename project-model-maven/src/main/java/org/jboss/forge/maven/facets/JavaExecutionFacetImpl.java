/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.JavaExecutionFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.OSUtils;

@Dependent
@Alias("forge.maven.JavaExecutionFacet")
@RequiresFacet(MavenCoreFacet.class)
public class JavaExecutionFacetImpl extends BaseFacet implements JavaExecutionFacet
{
   @Inject
   ShellPrintWriter out;

   @Inject
   Shell shell;

   @Override
   public void executeProjectClass(final String fullyQualifiedClassName, final String... arguments)
   {
      compileProjectClasses();

      CommandBuilder commandBuilder = CommandBuilder.getBuilder()
               .mainClass(fullyQualifiedClassName)
               .withArguments(arguments);

      if (shell.isVerbose())
      {
         commandBuilder.setVerbose();
      }

      executeClass(commandBuilder.build());
   }

   private void executeClass(final String[] mvnArguments)
   {
      project.getFacet(MavenCoreFacet.class).executeMaven(out, mvnArguments);
   }

   private void compileProjectClasses()
   {
      String[] compileArgs = { "test-compile" };

      project.getFacet(MavenCoreFacet.class).executeMaven(out, compileArgs);
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   private static class CommandBuilder
   {
      private final List<String> commands = new ArrayList<String>();

      public static CommandBuilder getBuilder()
      {
         CommandBuilder builder = new CommandBuilder();
         builder.commands.add("exec:java");
         builder.commands.add("-Dexec.classpathScope=test");

         return builder;
      }

      public CommandBuilder mainClass(final String mainClass)
      {
         commands.add("-Dexec.mainClass=" + mainClass);

         return this;
      }

      public CommandBuilder withArguments(final String[] arguments)
      {
         if (arguments.length > 0)
         {
            String quotes = OSUtils.isWindows() ? "\\\"" : "\"";
            StringBuilder argBuilder = new StringBuilder("-Dexec.args=").append(quotes);

            boolean first = true;
            for (String argument : arguments)
            {
               if (!first)
               {
                  argBuilder.append(" ");
               }
               argBuilder.append(argument);
               first = false;
            }

            argBuilder.append(quotes).append(" ");
            commands.add(argBuilder.toString());

         }

         return this;
      }

      public void setVerbose()
      {
         commands.add("-X");
      }

      public String[] build()
      {
         String[] args = new String[commands.size()];
         return commands.toArray(args);
      }
   }
}
