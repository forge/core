package org.jboss.forge.addon.ui.converters;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.ui.input.types.JavaVariableName;

public class VariableNameConverterGenerator implements ConverterGenerator
{
   @Inject
   private Instance<VariableNameConverter> variableNameConverter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return JavaVariableName.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return variableNameConverter.get();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return VariableNameConverter.class;
   }

}
