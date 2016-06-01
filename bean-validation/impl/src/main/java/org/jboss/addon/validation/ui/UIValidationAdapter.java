/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.addon.validation.ui;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * A {@link UIValidator} adapter that validates using Bean Validation 1.1
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class UIValidationAdapter implements UIValidator
{
   private final Validator validator;
   private final InputComponent<?, ?> input;
   private final Class<?> commandClass;
   private Logger log = Logger.getLogger(UIValidationAdapter.class.getName());

   public UIValidationAdapter(Validator validator, InputComponent<?, ?> input, Class<?> commandClass)
   {
      super();
      this.validator = validator;
      this.input = input;
      this.commandClass = commandClass;
   }

   @Override
   public void validate(final UIValidationContext context)
   {
      try
      {
         // Needed to avoid the javax.el.ExpressionFactory instance not found error
         ClassLoaders.executeIn(getClass().getClassLoader(), new Callable<Void>()
         {
            @Override
            public Void call()
            {
               String inputLabel = InputComponents.getLabelFor(input, true);
               for (ConstraintViolation<?> violation : validator.validateValue(commandClass, input.getName(), input))
               {
                  context.addValidationError(input, inputLabel + " " + violation.getMessage());
               }
               return null;
            }
         });
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Error while validating using BV", e);
      }
   }
}
