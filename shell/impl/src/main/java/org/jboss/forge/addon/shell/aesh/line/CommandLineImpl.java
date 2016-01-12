/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.line;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.forge.addon.shell.line.CommandOption;

/**
 * Default {@link org.jboss.forge.addon.shell.line.CommandLine} implementation
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandLineImpl implements org.jboss.forge.addon.shell.line.CommandLine
{
   private final CommandLine<?> cmdLine;

   public CommandLineImpl(CommandLine<?> cmdLine)
   {
      super();
      this.cmdLine = cmdLine;
   }

   @Override
   public CommandOption getArgument()
   {
      ProcessedOption argument = cmdLine.getArgument();
      return argument == null || argument.getValue() == null ? null : new CommandOptionImpl(argument);
   }

   @Override
   public List<CommandOption> getOptions()
   {
      return cmdLine
               .getOptions()
               .stream()
               .map((option) -> new CommandOptionImpl(option))
               .collect(Collectors.toList());
   }

   @Override
   public String toString()
   {
      return "CommandLineImpl [cmdLine=" + cmdLine + "]";
   }
}