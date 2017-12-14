/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.result.Result;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractCommandControllerDecorator implements CommandController
{
   private final CommandController delegate;

   protected AbstractCommandControllerDecorator(CommandController delegate)
   {
      this.delegate = delegate;
   }

   /**
    * @return the delegate
    */
   protected CommandController getDelegate()
   {
      return delegate;
   }

   @Override
   public void initialize() throws Exception
   {
      delegate.initialize();
   }

   @Override
   public boolean isInitialized()
   {
      return delegate.isInitialized();
   }

   @Override
   public Result execute() throws Exception
   {
      return delegate.execute();
   }

   @Override
   public List<UIMessage> validate()
   {
      return delegate.validate();
   }

   @Override
   public List<UIMessage> validate(InputComponent<?, ?> input)
   {
      return delegate.validate(input);
   }

   @Override
   public boolean isValid()
   {
      return delegate.isValid();
   }

   @Override
   public CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException
   {
      delegate.setValueFor(inputName, value);
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      return delegate.getValueFor(inputName);
   }

   @Override
   public Map<String, InputComponent<?, ?>> getInputs()
   {
      return delegate.getInputs();
   }

   @Override
   public InputComponent<?, ?> getInput(String inputName)
   {
      return delegate.getInput(inputName);
   }

   @Override
   public boolean hasInput(String inputName)
   {
      return delegate.hasInput(inputName);
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return delegate.getMetadata();
   }

   @Override
   public boolean isEnabled()
   {
      return delegate.isEnabled();
   }

   @Override
   public UICommand getCommand()
   {
      return delegate.getCommand();
   }

   @Override
   public UIContext getContext()
   {
      return delegate.getContext();
   }

   @Override
   public boolean canExecute()
   {
      return delegate.canExecute();
   }

   @Override
   public void close() throws Exception
   {
      delegate.close();
   }
}