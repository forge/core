package org.jboss.forge.addon.ui.validators;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.types.PatternedStringInput;
import org.jboss.forge.addon.ui.util.InputComponents;

public abstract class PatternBasedValidator<VALUETYPE extends PatternedStringInput> implements UIValidator
{

   private UIInput<VALUETYPE> input;

   public PatternBasedValidator(UIInput<VALUETYPE> input)
   {
      super();
      this.input = input;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      Object rawValue = InputComponents.getValueFor(input);
      if (rawValue instanceof PatternedStringInput)
      {
         String value = ((PatternedStringInput) rawValue).getValidatableValue();
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

   protected void setInput(UIInput<VALUETYPE> input)
   {
      this.input = input;
   }

   protected abstract Iterable<String> getPatterns();

   protected abstract String getMessage(String value);

}