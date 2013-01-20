package org.jboss.forge.convert.impl;

import org.jboss.forge.convert.BaseConverter;

public class ToStringConverter<S> extends BaseConverter<S, String>
{
   public ToStringConverter(Class<S> sourceType)
   {
      super(sourceType, String.class);
   }

   @Override
   public String convert(Object source)
   {
      if (source != null)
         return source.toString();
      return null;
   }
}
