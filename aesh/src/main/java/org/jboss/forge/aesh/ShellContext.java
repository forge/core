/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UISelection;
import org.jboss.forge.ui.UIValidationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellContext implements UIValidationContext, UIContext, UIBuilder
{

   private boolean standalone = false;
   private List<UIInput<?>> inputs = new ArrayList<UIInput<?>>();

   public ShellContext()
   {
   }

   public boolean isStandalone()
   {
      return standalone;
   }

   public void setStandalone(boolean standalone)
   {
      this.standalone = standalone;
   }

   @Override
   public UIBuilder add(UIInput<?> input)
   {
      inputs.add(input);
      return this;
   }

   public List<UIInput<?>> getInputs()
   {
      return inputs;
   }

   public UIInput<?> findInput(String name)
   {
      for (UIInput<?> input : inputs)
      {
         if (input.getName().equals(name))
            return input;
      }
      return null;
   }

   @Override
   public void addValidationError(UIInput<?> input, String errorMessage)
   {
      // TODO: ignoring errorMessage for now
      inputs.add(input);
   }

   @Override
   public UIBuilder getUIBuilder()
   {
      return this;
   }

   @Override
   public UISelection<Object> getInitialSelection()
   {
      throw new IllegalStateException("not implemented");
   }
}
