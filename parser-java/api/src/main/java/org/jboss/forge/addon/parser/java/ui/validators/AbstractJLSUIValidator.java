/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.validators;

import org.jboss.forge.addon.parser.java.utils.ValidationResult;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractJLSUIValidator implements UIValidator
{
   @Override
   public void validate(UIValidationContext context)
   {
      InputComponent<?, ?> currentInputComponent = context.getCurrentInputComponent();
      Object value = InputComponents.getValueFor(currentInputComponent);
      if (value != null)
      {
         ValidationResult result = validate(value.toString());
         switch (result.getType())
         {
         case ERROR:
            context.addValidationError(currentInputComponent, result.getMessage());
            break;
         case WARNING:
            context.addValidationWarning(currentInputComponent, result.getMessage());
            break;
         default:
            break;
         }
      }
   }

   protected abstract ValidationResult validate(String value);
}
