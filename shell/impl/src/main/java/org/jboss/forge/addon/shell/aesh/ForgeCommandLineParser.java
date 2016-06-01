/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.parser.AeshCommandLineParser;
import org.jboss.aesh.cl.populator.CommandPopulator;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ForgeCommandLineParser extends AeshCommandLineParser
{
   private final CommandPopulator commandPopulator;

   public ForgeCommandLineParser(ProcessedCommand command, CommandLineUtil commandLineUtil,
            Map<String, InputComponent<?, ?>> inputs, ShellContext shellContext)
   {
      super(command);
      this.commandPopulator = new ForgeCommandPopulator(commandLineUtil, inputs, shellContext);
   }

   @Override
   public CommandPopulator getCommandPopulator()
   {
      return commandPopulator;
   }

}
