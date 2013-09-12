package org.jboss.forge.addon.ui.converters;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.types.JavaVariableName;

public class VariableNameConverter implements Converter<String, JavaVariableName>
{

   @Override
   public JavaVariableName convert(String source)
   {
      JavaVariableName variableName = new JavaVariableName();
      variableName.setVariableName(source);
      return variableName;
   }

}
