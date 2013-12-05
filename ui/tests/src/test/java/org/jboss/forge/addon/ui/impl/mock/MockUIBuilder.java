/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.mock;

import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIBuilder implements UIBuilder
{
   private final UIContext context;
   private List<InputComponent<?, ?>> inputs = new LinkedList<InputComponent<?, ?>>();

   public MockUIBuilder(UIContext context)
   {
      this.context = context;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public UIBuilder add(InputComponent<?, ?> input)
   {
      inputs.add(input);
      return this;
   }

   public List<InputComponent<?, ?>> getInputs()
   {
      return inputs;
   }

}
