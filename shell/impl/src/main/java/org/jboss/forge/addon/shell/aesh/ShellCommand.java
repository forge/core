/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.shell.ui.ShellUIBuilderImpl;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Encapsulates a {@link UICommand} to be useful in a Shell context
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellCommand
{
   private final UICommand command;
   private final UICommandMetadata metadata;
   private final ShellContextImpl context;
   private final CommandLineParser commandLineParser;
   private Map<String, InputComponent<?, Object>> inputs;
   private CommandLineUtil commandLineUtil;

   /**
    * Creates a new {@link ShellCommand} based on the shell and initial selection
    */
   public ShellCommand(UICommand command, ShellContextImpl shellContext, CommandLineUtil commandLineUtil)
   {
      this.command = command;
      UICommandMetadata commandMetadata = command.getMetadata();
      this.metadata = Metadata.from(commandMetadata).name(ShellUtil.shellifyName(commandMetadata.getName()));

      // Create ShellContext
      this.context = shellContext;

      // Initialize UICommand
      ShellUIBuilderImpl builder = new ShellUIBuilderImpl(this.context);
      try
      {
         this.command.initializeUI(builder);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while initializing command", e);
      }
      this.inputs = builder.getComponentMap();
      this.commandLineParser = commandLineUtil.generateParser(this.command, inputs);
   }

   UICommandMetadata getMetadata()
   {
      return metadata;
   }

   UICommand getCommand()
   {
      return command;
   }

   Map<String, InputComponent<?, Object>> getInputs()
   {
      return inputs;
   }

   CommandLineParser getCommandLineParser()
   {
      return commandLineParser;
   }

   public Result run(ConsoleOutput consoleOutput, CommandLine commandLine) throws Exception
   {
      context.setConsoleOutput(consoleOutput);
      commandLineUtil.populateUIInputs(commandLine, inputs);
      Result result = command.execute(context);
      if (result != null &&
               result.getMessage() != null && result.getMessage().length() > 0)
         context.getProvider().getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
      return result;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof ShellCommand))
         return false;

      ShellCommand that = (ShellCommand) o;

      if (!getMetadata().getName().equals(that.getMetadata().getName()))
         return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return getMetadata().getName().hashCode();
   }

   @Override
   public String toString()
   {
      return getMetadata().getName();
   }
}
