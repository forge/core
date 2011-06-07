/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.maven.facets;

import java.io.IOException;
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
import org.jboss.forge.shell.util.NativeSystemCall;
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

   private String getMvnCommand()
   {
      return OSUtils.isWindows() ? "mvn.bat" : "mvn";
   }

   private void executeClass(final String[] mvnArguments)
   {
      try
      {
         NativeSystemCall.execFromPath(getMvnCommand(), mvnArguments, out, project.getProjectRoot());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private void compileProjectClasses()
   {
      String[] compileArgs = { "test-compile" };

      try
      {
         NativeSystemCall.execFromPath(getMvnCommand(), compileArgs, out, project.getProjectRoot());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error while invoking mvn test-compile", e);
      }
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
            StringBuilder argBuilder = new StringBuilder("-Dexec.args=\"");

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

            argBuilder.append("\" ");
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
