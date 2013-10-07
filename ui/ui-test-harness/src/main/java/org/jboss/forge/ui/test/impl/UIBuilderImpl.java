/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.input.InputComponent;

public class UIBuilderImpl implements UIBuilder
{
   private final Map<String, InputComponent<?, ?>> components = new HashMap<String, InputComponent<?, ?>>();
   private final UIContextImpl contextImpl;
   private final UICommand command;

   public UIBuilderImpl(UIContextImpl contextImpl, UICommand command)
   {
      this.contextImpl = contextImpl;
      this.command = command;
   }

   @Override
   public UIContextImpl getUIContext()
   {
      return contextImpl;
   }

   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      components.put(input.getName(), input);
      return this;
   }

   public InputComponent<?, ?> getComponentNamed(String name)
   {
      return components.get(name);
   }

   public UICommand getCommand()
   {
      return command;
   }

   public Iterable<InputComponent<?, ?>> getInputs()
   {
      return components.values();
   }

}
