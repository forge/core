/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.validate;

import java.util.Collection;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidationListener;
import org.jboss.forge.furnace.util.Strings;

/**
 * Adds a deprecation warning to a given command
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DeprecationWarningValidationListener implements UIValidationListener
{

   @Override
   public void preValidate(UIValidationContext context, UICommand command, Collection<InputComponent<?, ?>> inputs)
   {
      // Do nothing
   }

   @Override
   public void postValidate(UIValidationContext context, UICommand command, Collection<InputComponent<?, ?>> inputs)
   {
      UIContext uiContext = context.getUIContext();
      UICommandMetadata metadata = command.getMetadata(uiContext);
      // Check if command is deprecated
      if (metadata.isDeprecated())
      {
         String msg = String.format(
                  "The command '%s' is deprecated and will be removed in future versions.", metadata.getName());
         if (!Strings.isNullOrEmpty(metadata.getDeprecatedMessage()))
         {
            msg += " " + metadata.getDeprecatedMessage();
         }
         context.addValidationWarning(null, msg);
      }
      // Check if input is deprecated
      for (InputComponent<?, ?> input : inputs)
      {
         // Only inputs with a value set are validated
         if (input.isEnabled() && input.isDeprecated() && input.hasValue())
         {
            String name = uiContext.getProvider().isGUI() ? InputComponents.getLabelFor(input, false)
                     : input.getName();
            String msg = String.format(
                     "The parameter '%s' from command '%s' is deprecated and will be removed in future versions.", name,
                     metadata.getName());
            if (!Strings.isNullOrEmpty(input.getDeprecatedMessage()))
            {
               msg += " " + input.getDeprecatedMessage();
            }
            context.addValidationWarning(input, msg);
         }
      }

   }
}
