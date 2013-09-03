/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.parser.AeshCommandLineParser;
import org.jboss.aesh.cl.parser.CommandPopulator;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandLineParser extends AeshCommandLineParser
{
   private final CommandPopulator commandPopulator;

   public ForgeCommandLineParser(ProcessedCommand command, CommandLineUtil commandLineUtil,
            Map<String, InputComponent<?, Object>> inputs)
   {
      super(command);
      this.commandPopulator = new ForgeCommandPopulator(commandLineUtil, inputs);
   }

   @Override
   public CommandPopulator getCommandPopulator()
   {
      return commandPopulator;
   }

}
