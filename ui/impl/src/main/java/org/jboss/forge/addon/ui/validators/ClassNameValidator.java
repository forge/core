package org.jboss.forge.addon.ui.validators;

import java.util.Arrays;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.input.InputComponent;

public class ClassNameValidator extends PatternBasedValidator implements UIValidator
{

   String[] patterns = new String[] { "(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]",
            "^(?!.*\\b(" + Patterns.JAVA_KEYWORDS + ")\\b.*).*$" };

   public ClassNameValidator(InputComponent<?, ?> input)
   {
      super(input);
   }

   @Override
   protected Iterable<String> getPatterns()
   {
      return Arrays.asList(patterns);
   }

   @Override
   protected String getMessage(String value)
   {
      return value + " is not a valid class name for " + getInput().getLabel();
   }

}
