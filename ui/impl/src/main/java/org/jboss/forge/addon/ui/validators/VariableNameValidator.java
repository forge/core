package org.jboss.forge.addon.ui.validators;

import java.util.Arrays;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.types.JavaVariableName;

public class VariableNameValidator extends PatternBasedValidator<JavaVariableName> implements UIValidator
{

   private String[] patterns = new String[] { "^(?!(" + Patterns.JAVA_KEYWORDS + ")$)[A-Za-z0-9$_]+$" };

   public VariableNameValidator(UIInput<JavaVariableName> input)
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
      return value + " is not a valid Java variable name for " + getInput().getLabel();
   }

}
