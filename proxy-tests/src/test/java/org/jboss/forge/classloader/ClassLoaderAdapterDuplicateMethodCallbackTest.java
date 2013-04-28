/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader;

import org.jboss.forge.classloader.mock.MockParentInterface1;
import org.jboss.forge.classloader.mock.MockService2;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderAdapterDuplicateMethodCallbackTest
{
   @Test
   public void testNestedDupicateProxyAdapterCallback() throws Exception
   {
      ClassLoader loader = ClassLoaderAdapterDuplicateMethodCallbackTest.class.getClassLoader();
      MockParentInterface1 original = new MockService2(true);
      MockParentInterface1 object = ClassLoaderAdapterCallback.enhance(loader, loader, original);
      Assert.assertNotSame(object, original);
   }
}