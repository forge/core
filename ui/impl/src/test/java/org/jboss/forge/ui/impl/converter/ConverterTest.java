/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

import java.util.HashMap;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Vector;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.converter.ConverterNotFoundException;
import org.jboss.forge.ui.converter.ConverterRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConverterTest
{
   private ConverterRegistry registry;

   @Before
   public void setUp()
   {
      registry = ServiceLoader.load(ConverterRegistry.class).iterator().next();
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      String input = "123";
      Integer expected = 123;
      Converter<String, Integer> converter = registry.getConverter(String.class, Integer.class);
      Assert.assertEquals(expected, converter.convert(input));
   }

   @Test
   public void testStringToObjectConversion() throws Exception
   {
      String input = "123";
      String expected = "valueOf_123";
      Converter<String, SimpleBean> converter = registry.getConverter(String.class, SimpleBean.class);
      SimpleBean obj = converter.convert(input);
      Assert.assertNotNull(obj);
      Assert.assertEquals(expected, obj.getValue());
   }

   @Test
   public void testObjectToObjectConversion() throws Exception
   {
      SimpleBean input = new SimpleBean("value");
      Converter<SimpleBean, AnotherBean> converter = registry
               .getConverter(SimpleBean.class, AnotherBean.class);
      AnotherBean obj = converter.convert(input);
      Assert.assertNotNull(obj);
      Assert.assertEquals(obj.getValue(), input.getValue());
   }

   @Test(expected = ConverterNotFoundException.class)
   public void testConverterNotFound() throws Exception
   {
      registry.getConverter(AnotherBean.class, Vector.class);
   }

   @Test
   public void testEnumConverter() throws Exception
   {
      Converter<String, SimpleEnum> converter = registry.getConverter(String.class, SimpleEnum.class);
      SimpleEnum result = converter.convert("STARTED");
      Assert.assertEquals(SimpleEnum.STARTED, result);
   }

   @SuppressWarnings("rawtypes")
   @Test
   public void testCustomConverter() throws Exception
   {
      Converter<Properties, HashMap> converter = new Converter<Properties, HashMap>()
      {
         @Override
         @SuppressWarnings("unchecked")
         public HashMap convert(Properties source) throws Exception
         {
            HashMap map = new HashMap();
            map.putAll(source);
            return map;
         }
      };
      registry.addConverter(Properties.class, HashMap.class, converter);

      Converter<Properties, HashMap> converterFromRegistry = registry.getConverter(Properties.class, HashMap.class);
      Assert.assertSame(converter, converterFromRegistry);

      Properties props = new Properties();
      props.setProperty("propOne", "valOne");
      HashMap map = converterFromRegistry.convert(props);
      Assert.assertEquals(1, map.size());
      Assert.assertEquals("valOne", map.get("propOne"));
   }
}
