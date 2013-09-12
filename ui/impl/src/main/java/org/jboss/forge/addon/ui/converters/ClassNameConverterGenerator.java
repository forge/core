package org.jboss.forge.addon.ui.converters;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.ui.input.types.JavaClassName;

public class ClassNameConverterGenerator implements ConverterGenerator
{
   @Inject
   private Instance<ClassNameConverter> classNameConverter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return JavaClassName.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return classNameConverter.get();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return ClassNameConverter.class;
   }

}
