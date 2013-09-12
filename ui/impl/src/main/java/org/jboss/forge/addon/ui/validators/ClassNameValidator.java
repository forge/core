package org.jboss.forge.addon.ui.validators;

import java.util.Arrays;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.types.JavaClassName;

public class ClassNameValidator extends PatternBasedValidator<JavaClassName> implements UIValidator
{

   String[] patterns = new String[] { "(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]",
            "^(?!.*\\b(" + Patterns.JAVA_KEYWORDS + ")\\b.*).*$" };

   public ClassNameValidator(UIInput<JavaClassName> input)
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
      return "\"" + value + "\" is not a valid class name for " + getInput().getLabel();
   }

}
