/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.convert;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.dependencies.Dependency;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyConverterGenerator implements ConverterGenerator
{
   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return Dependency.class.isAssignableFrom(target) && String.class.isAssignableFrom(source);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return new DependencyConverter();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return DependencyConverter.class;
   }
}
