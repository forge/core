/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.proxy;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class ProxiesTest
{

   @Test
   public void testUnwrapProxyClassName()
   {
      Bean enhancedObj = Proxies.enhance(Bean.class, new ForgeProxy()
      {

         @Override
         public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
         {
            return null;
         }

         @Override
         public Object getDelegate()
         {
            return null;
         }
      });
      Assert.assertNotEquals(Bean.class.getName(), enhancedObj.getClass().getName());
      String result = Proxies.unwrapProxyClassName(enhancedObj.getClass());
      Assert.assertEquals(Bean.class.getName(), result);
   }

   @Test
   public void testAreEquivalent()
   {
      Bean enhancedObj = Proxies.enhance(Bean.class, new ForgeProxy()
      {

         @Override
         public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
         {
            return proceed.invoke(self, args);
         }

         @Override
         public Object getDelegate()
         {
            return null;
         }
      });
      enhancedObj.setAtt("String");
      Bean bean2 = new Bean();
      bean2.setAtt("String");

      Assert.assertTrue(Proxies.areEquivalent(enhancedObj, bean2));
   }

   @Test
   public void testIsInstance()
   {
      Bean enhancedObj = Proxies.enhance(Bean.class, new ForgeProxy()
      {

         @Override
         public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
         {
            return proceed.invoke(self, args);
         }

         @Override
         public Object getDelegate()
         {
            return null;
         }
      });
      Assert.assertTrue(Proxies.isInstance(Bean.class, enhancedObj));
   }

   @Test
   public void testIsInstantiable() throws Exception
   {
      Assert.assertFalse(Proxies.isInstantiable(TypeWithNonDefaultConstructor.class));
      Assert.assertTrue(Proxies.isInstantiable(Bean.class));
   }

   @Test
   public void testJavaLangAndJavaIOAreLanguageTypes() throws Exception
   {
      Object[] foo = new Object[] {};
      Assert.assertTrue(Proxies.isLanguageType(foo.getClass()));
      Assert.assertTrue(Proxies.isLanguageType(Object.class));
      Assert.assertTrue(Proxies.isLanguageType(InputStream.class));
      Assert.assertTrue(Proxies.isLanguageType(Runnable.class));
      Assert.assertTrue(Proxies.isLanguageType(String.class));
      Assert.assertTrue(Proxies.isLanguageType(Class.class));
      Assert.assertTrue(Proxies.isLanguageType(ClassLoader.class));
      Assert.assertFalse(Proxies.isLanguageType(BigDecimal.class));
      Assert.assertFalse(Proxies.isLanguageType(List.class));
      Assert.assertFalse(Proxies.isLanguageType(Set.class));
      Assert.assertFalse(Proxies.isLanguageType(Map.class));
   }

}
