/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

import java.util.ServiceLoader;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.converter.ConverterRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConverterTest
{
   private ConverterRegistry converterRegistry;

   @Before
   public void setUp()
   {
      converterRegistry = ServiceLoader.load(ConverterRegistry.class).iterator().next();
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      String input = "123";
      Integer expected = 123;
      Converter<String, Integer> converter = converterRegistry.getConverter(String.class, Integer.class);
      Assert.assertEquals(expected, converter.convert(input));
   }

   @Test
   public void testStringToObjectConversion() throws Exception
   {
      String input = "123";
      String expected = "valueOf_123";
      Converter<String, SimpleBean> converter = converterRegistry.getConverter(String.class, SimpleBean.class);
      SimpleBean obj = converter.convert(input);
      Assert.assertNotNull(obj);
      Assert.assertEquals(expected, obj.getValue());
   }

   @Test
   public void testObjectToObjectConversion() throws Exception
   {
      SimpleBean input = new SimpleBean("value");
      Converter<SimpleBean, AnotherBean> converter = converterRegistry
               .getConverter(SimpleBean.class, AnotherBean.class);
      AnotherBean obj = converter.convert(input);
      Assert.assertNotNull(obj);
      Assert.assertEquals(obj.getValue(), input.getValue());
   }

}
