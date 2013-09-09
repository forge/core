package org.jboss.forge.addon.javaee.validation.ui.setup;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;

public class ClassInputValidator implements UIValidator
{
   private String[] patterns = new String[] { "(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]",
            "^(?!.*\\b(" + Patterns.JAVA_KEYWORDS + ")\\b.*).*$" };
   private UIInput<String> component;

   public ClassInputValidator(UIInput<String> component)
   {
      this.component = component;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String className = component().getValue();
      if (className != null && !matchesValidClassName(className))
      {
         context.addValidationError(component(), className + " is not a valid class name for " + component.getLabel());
      }
   }

   private UIInput<String> component()
   {
      return component;
   }

   public boolean matchesValidClassName(final String value)
   {
      if (value == null)
      {
         return false;
      }

      for (int i = 0; i < patterns.length; i++)
      {
         if (!value.matches(patterns[i]))
         {
            return false;
         }
      }
      return true;
   }
}