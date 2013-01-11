package org.jboss.forge.ui.impl.converter;

import javax.enterprise.inject.Typed;

import org.jboss.forge.ui.converter.Converter;

@Typed()
public class ConverterPlaceholderImpl<T> implements Converter<T, T>
{
   @Override
   public T convert(Object source) throws Exception
   {
      throw new IllegalStateException("Should never be invoked.");
   }
}
