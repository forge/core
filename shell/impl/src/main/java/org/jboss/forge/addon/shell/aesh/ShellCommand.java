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
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellUIBuilderImpl;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Encapsulates a {@link UICommand} to be useful in a Shell context
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellCommand
{
   private final String name;
   private final UICommand command;
   private final ShellContext context;
   private final CommandLineParser commandLineParser;
   private Map<String, InputComponent<?, Object>> inputs;
   private CommandLineUtil commandLineUtil;

   /**
    * Creates a new {@link ShellCommand} based on the shell and initial selection
    */
   public ShellCommand(UICommand command, ShellContext shellContext, CommandLineUtil commandLineUtil)
   {
      this.command = command;
      UICommandMetadata commandMetadata = command.getMetadata();
      this.name = ShellUtil.shellifyName(commandMetadata.getName());

      this.context = shellContext;
      // Initialize UICommand
      ShellUIBuilderImpl builder = new ShellUIBuilderImpl(shellContext);
      try
      {
         this.command.initializeUI(builder);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while initializing command", e);
      }
      this.inputs = builder.getComponentMap();
      this.commandLineUtil = commandLineUtil;
      this.commandLineParser = commandLineUtil.generateParser(this.command, inputs);
   }

   public UICommand getCommand()
   {
      return command;
   }

   public String getName()
   {
      return name;
   }

   public Map<String, InputComponent<?, Object>> getInputs()
   {
      return inputs;
   }

   public CommandLineParser getCommandLineParser()
   {
      return commandLineParser;
   }

   public void populateInputs(String line) throws CommandLineParserException
   {
      CommandLine commandLine = this.commandLineParser.parse(line);
      this.commandLineUtil.populateUIInputs(commandLine, inputs);
   }

   public Result execute() throws Exception
   {
      return command.execute(context);
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof ShellCommand))
         return false;

      ShellCommand that = (ShellCommand) o;

      if (!getName().equals(that.getName()))
         return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return getName().hashCode();
   }

   @Override
   public String toString()
   {
      return getName();
   }
}
