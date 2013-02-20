/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.proxy;

import java.lang.reflect.Method;

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

}
