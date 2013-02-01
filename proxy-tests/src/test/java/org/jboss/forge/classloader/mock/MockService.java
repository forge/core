/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class MockService implements MockInterface1, MockInterface2
{
   public MockResult getResult()
   {
      return new MockResult();
   }

   public MockResult getResultEnhanced()
   {
      final MockResult internal = new MockResult();
      MockResult delegate = (MockResult) Enhancer.create(MockResult.class, new LazyLoader()
      {
         @Override
         public Object loadObject() throws Exception
         {
            return internal;
         }
      });

      return delegate;
   }

   public Object getResultFinalReturnTypeObject()
   {
      return new MockFinalResult();
   }

   public MockFinalResult getResultFinalReturnType()
   {
      return new MockFinalResult();
   }

   public Result getResultInterfaceFinalImpl()
   {
      return new MockFinalResult();
   }

   public Object getResultEnhancedReturnTypeObject()
   {
      return getResultEnhanced();
   }

   public String echo(String value)
   {
      return value;
   }
}
