/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui.setup;

import org.jboss.forge.addon.parser.java.utils.JLSValidator;
import org.jboss.forge.addon.parser.java.utils.ResultType;
import org.jboss.forge.addon.parser.java.utils.ValidationResult;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.util.Strings;

public class ClassInputValidator implements UIValidator
{
   private UIInput<String> component;

   public ClassInputValidator(UIInput<String> component)
   {
      this.component = component;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String className = component().getValue();
      if (!Strings.isNullOrEmpty(className))
      {
         ValidationResult result = JLSValidator.validateClassName(className);
         if (!result.getType().equals(ResultType.INFO))
         {
            context.addValidationError(component(), result.getMessage());
         }
      }
   }

   private UIInput<String> component()
   {
      return component;
   }

}