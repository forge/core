/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.AbstractUIContext;
import org.jboss.forge.ui.context.UISelection;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.InputComponent;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellContext extends AbstractUIContext implements UIValidationContext, UIContext, UIBuilder
{

   private boolean standalone = false;
   private List<InputComponent<?, Object>> inputs = new ArrayList<InputComponent<?, Object>>();
   private ForgeShell aeshell;
   private CommandLineParser parser;
   private ConsoleOutput consoleOutput;

   public ShellContext(ForgeShell aeshell)
   {
      this.aeshell = aeshell;
   }

   public boolean isStandalone()
   {
      return standalone;
   }

   public void setStandalone(boolean standalone)
   {
      this.standalone = standalone;
   }

   public ForgeShell getShell()
   {
      return aeshell;
   }

   public void setParser(CommandLineParser parser)
   {
      this.parser = parser;
   }

   public CommandLineParser getParser()
   {
      return parser;
   }

   public void setConsoleOutput(ConsoleOutput output)
   {
      this.consoleOutput = output;
   }

   public ConsoleOutput getConsoleOutput()
   {
      return consoleOutput;
   }

   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      inputs.add((InputComponent<?, Object>) input);
      return this;
   }

   public List<InputComponent<?, Object>> getInputs()
   {
      return inputs;
   }

   public InputComponent<?, ?> findInput(String name)
   {
      for (InputComponent<?, ?> input : inputs)
      {
         if (input.getName().equals(name))
            return input;
      }
      return null;
   }

   @Override
   public void addValidationError(InputComponent<?, ?> input, String errorMessage)
   {
      // TODO: ignoring errorMessage for now
      inputs.add((InputComponent<?, Object>) input);
   }

   @Override
   public UIContext getUIContext()
   {
      return this;
   }

   public <T> UISelection<T> getCurrentSelection()
   {
      return null; // not implemented
   }

}
