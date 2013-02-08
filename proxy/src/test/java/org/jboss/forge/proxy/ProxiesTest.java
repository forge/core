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
      String result = Proxies.unwrapProxyClassName(enhancedObj.getClass());
      Assert.assertEquals(Bean.class.getName(), result);
   }

}
