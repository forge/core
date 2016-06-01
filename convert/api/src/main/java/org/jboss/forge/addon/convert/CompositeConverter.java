/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Vetoed;

/**
 * A {@link CompositeConverter} converts elements in the specified sequence. <br/>
 *
 * The result of a {@link Converter} serves as the input for the next converter
 *
 * <br>
 * The {@link CompositeConverter#convert(Object)} method always returns the last object converted
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Vetoed
@SuppressWarnings("rawtypes")
public class CompositeConverter implements Converter<Object, Object>
{
   private List<Converter> converters;

   public CompositeConverter(Converter... converters)
   {
      this.converters = Arrays.asList(converters);
   }

   /**
    * This method always returns the last object converted from the list
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object convert(Object source)
   {
      Object value = source;
      for (Converter<Object, Object> converter : converters)
      {
         if (converter != null)
         {
            value = converter.convert(value);
         }
      }
      return value;
   }
}
