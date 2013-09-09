package org.jboss.forge.addon.ui.validators;

import java.util.Arrays;

import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.input.InputComponent;

public class MavenDependencyIdValidator extends PatternBasedValidator implements UIValidator
{

   private String[] patterns = new String[] { "[^:]+:[^:]+:?([^:]+:?){0,3}" };

   public MavenDependencyIdValidator(InputComponent<?, ?> input)
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
      return value + " is not a valid Maven dependency Id for " + getInput().getLabel();
   }

}
