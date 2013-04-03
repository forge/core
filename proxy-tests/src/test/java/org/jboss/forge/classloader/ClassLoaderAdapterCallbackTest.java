/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader;

import java.lang.reflect.Method;

import org.jboss.forge.classloader.mock.MockResult;
import org.jboss.forge.classloader.mock.MockService;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;
import org.jboss.forge.proxy.ForgeProxy;
import org.jboss.forge.proxy.Proxies;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderAdapterCallbackTest
{
   ForgeProxy handler = new ForgeProxy()
   {
      @Override
      public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
      {
         return null;
      }

      @Override
      public Object getDelegate()
      {
         return new MockService();
      }
   };

   @Test
   public void testNestedProxy() throws Exception
   {
      Object object = Proxies.enhance(MockService.class, handler);
      Proxies.enhance(object.getClass(), handler);
   }

   @Test
   public void testNestedDupicateProxyAdapterCallback() throws Exception
   {
      ClassLoader loader = ClassLoaderAdapterCallbackTest.class.getClassLoader();
      MockService original = new MockService();
      MockService object = ClassLoaderAdapterCallback.enhance(loader, loader, original, MockService.class);
      MockService object2 = ClassLoaderAdapterCallback.enhance(loader, loader, object, object.getClass());
      Assert.assertNotSame(object, object2);
   }

   @Test
   public void testProxyAdapterCallbackNestedInteraction() throws Exception
   {
      ClassLoader loader = ClassLoaderAdapterCallbackTest.class.getClassLoader();
      MockService original = new MockService();
      MockService object = ClassLoaderAdapterCallback.enhance(loader, loader, original, MockService.class);
      MockResult result = object.getResult();
      Assert.assertNotSame(result, original.getResult());
   }

   @Test
   public void testNestedProxyAdapterCallback() throws Exception
   {
      MockService object = Proxies.enhance(MockService.class, handler);
      ClassLoader loader = ClassLoaderAdapterCallbackTest.class.getClassLoader();
      MockService object2 = ClassLoaderAdapterCallback.enhance(loader, loader, object, MockService.class);
      Assert.assertNotSame(object, object2);
   }
}