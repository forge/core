/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class CompositeConverterTest
{
   @SuppressWarnings("rawtypes")
   @Test
   public void testCompositeConverter()
   {
      final AtomicInteger counter = new AtomicInteger();
      Converter[] converters = {
               new Converter<String, File>()
               {
                  @Override
                  public File convert(String source)
                  {
                     counter.incrementAndGet();
                     return new File(source);
                  }
               },
               new Converter<File, String>()
               {
                  @Override
                  public String convert(File source)
                  {
                     counter.incrementAndGet();
                     return source.getAbsolutePath();
                  }
               } };

      CompositeConverter compositeConverter = new CompositeConverter(converters);
      Object result = compositeConverter.convert(".");
      Assert.assertTrue(result instanceof String);
      Assert.assertEquals(2, counter.intValue());
   }
}
