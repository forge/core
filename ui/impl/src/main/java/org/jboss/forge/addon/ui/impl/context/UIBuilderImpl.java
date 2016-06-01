/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.context;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;

/**
 * Implementation of the {@link UIBuilder} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIBuilderImpl implements UIBuilder
{
   private final UIContext context;
   private final InputComponentFactory inputComponentFactory;
   private Map<String, InputComponent<?, ?>> inputs = new LinkedHashMap<>();

   public UIBuilderImpl(UIContext context, InputComponentFactory inputComponentFactory)
   {
      this.context = context;
      this.inputComponentFactory = inputComponentFactory;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      inputs.put(input.getName(), input);
      return this;
   }

   public Map<String, InputComponent<?, ?>> getInputs()
   {
      return inputs;
   }

   @Override
   public InputComponentFactory getInputComponentFactory()
   {
      return inputComponentFactory;
   }
}
