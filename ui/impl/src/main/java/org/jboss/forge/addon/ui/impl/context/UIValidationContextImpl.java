/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.context;

import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.furnace.util.Assert;

public class UIValidationContextImpl implements UIValidationContext
{
   private List<String> errors;
   private List<String> warnings;
   private List<String> informations;
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
      if (errorMessage == null || errorMessage.isEmpty())
      {
         getErrors().add(
                  "An internal error occurred and the original message should have been displayed in here. Check logs");
         Thread.dumpStack();
      }
      else
      {
         getErrors().add(errorMessage);
      }
   }

   @Override
   public void addValidationWarning(InputComponent<?, ?> input, String warningMessage)
   {
      Assert.notNull(warningMessage, "Warning Message cannot be null");
      getWarnings().add(warningMessage);
   }

   @Override
   public void addValidationInformation(InputComponent<?, ?> input, String infoMessage)
   {
      Assert.notNull(infoMessage, "Information Message cannot be null");
      getInformations().add(infoMessage);

   }

   public List<String> getErrors()
   {
      if (errors == null)
         errors = new LinkedList<String>();
      return errors;
   }

   public List<String> getWarnings()
   {
      if (warnings == null)
         warnings = new LinkedList<String>();
      return warnings;
   }

   public List<String> getInformations()
   {
      if (informations == null)
         informations = new LinkedList<String>();
      return informations;
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
}
