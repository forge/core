package org.jboss.forge.addon.ui.converters;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.ui.input.types.JavaPackageName;

public class PackageNameConverterGenerator implements ConverterGenerator
{
   @Inject
   private Instance<PackageNameConverter> packageNameConverter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return JavaPackageName.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return packageNameConverter.get();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return PackageNameConverter.class;
   }

}
