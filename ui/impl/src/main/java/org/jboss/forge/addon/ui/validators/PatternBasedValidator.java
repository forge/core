package org.jboss.forge.addon.ui.validators;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.util.InputComponents;

public abstract class PatternBasedValidator implements UIValidator
{

   private InputComponent<?, ?> input;

   public PatternBasedValidator(InputComponent<?, ?> input)
   {
      super();
      this.input = input;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      Object rawValue = InputComponents.getValueFor(input);
      if (rawValue instanceof String)
      {
         String value = (String) rawValue;
         if (!matches(value))
         {
            context.addValidationError(input, getMessage(value));
         }
      }
   }

   private boolean matches(final String value)
   {
      if (value == null)
      {
         return false;
      }

      for (String pattern : getPatterns())
      {
         if (!value.matches(pattern))
         {
            return false;
         }
      }
      return true;
   }

   protected InputComponent<?, ?> getInput()
   {
      return input;
   }

   protected void setInput(InputComponent<?, ?> input)
   {
      this.input = input;
   }

   protected abstract Iterable<String> getPatterns();

   protected abstract String getMessage(String value);

}