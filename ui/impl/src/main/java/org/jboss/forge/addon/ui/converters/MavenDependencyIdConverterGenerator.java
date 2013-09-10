package org.jboss.forge.addon.ui.converters;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;

public class MavenDependencyIdConverterGenerator implements ConverterGenerator
{
   @Inject
   private Instance<MavenDependencyIdConverter> mavenDependencyIdConverter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return MavenDependencyIdConverter.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return mavenDependencyIdConverter.get();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return MavenDependencyIdConverter.class;
   }

}
