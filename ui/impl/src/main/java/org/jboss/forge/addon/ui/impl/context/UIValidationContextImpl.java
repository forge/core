/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.context;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.impl.output.UIMessageImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.output.UIMessage.Severity;
import org.jboss.forge.furnace.util.Assert;

public class UIValidationContextImpl implements UIValidationContext
{
   private List<UIMessage> messages = new LinkedList<>();
   private UIContext context;
   private InputComponent<?, ?> currentInputComponent;

   public UIValidationContextImpl(UIContext context)
   {
      this.context = context;
   }

   @Override
   public void addValidationError(InputComponent<?, ?> input,
            String errorMessage)
   {
      Assert.notNull(errorMessage, "Error Message cannot be null");
      messages.add(new UIMessageImpl(Severity.ERROR, errorMessage, input));
   }

   @Override
   public void addValidationWarning(InputComponent<?, ?> input, String warningMessage)
   {
      Assert.notNull(warningMessage, "Warning Message cannot be null");
      messages.add(new UIMessageImpl(Severity.WARN, warningMessage, input));
   }

   @Override
   public void addValidationInformation(InputComponent<?, ?> input, String infoMessage)
   {
      Assert.notNull(infoMessage, "Information Message cannot be null");
      messages.add(new UIMessageImpl(Severity.INFO, infoMessage, input));

   }

   @Override
   public InputComponent<?, ?> getCurrentInputComponent()
   {
      return currentInputComponent;
   }

   public void setCurrentInputComponent(InputComponent<?, ?> currentInputComponent)
   {
      this.currentInputComponent = currentInputComponent;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public List<UIMessage> getMessages()
   {
      Collections.sort(messages, new Comparator<UIMessage>()
      {
         @Override
         public int compare(UIMessage o1, UIMessage o2)
         {
            return o1.getSeverity().compareTo(o2.getSeverity());
         }
      });
      return messages;
   }
}
