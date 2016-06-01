/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.convert;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.javaee.JavaEEFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SpecFacetConverterGenerator implements ConverterGenerator
{
   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return JavaEEFacet.class.isAssignableFrom(source) && String.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return new SpecFacetConverter();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return SpecFacetConverter.class;
   }

}
