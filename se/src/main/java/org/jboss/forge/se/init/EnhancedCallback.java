/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.se.init;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EnhancedCallback implements MethodInterceptor, Enhanced
{
   private Object delegate;

   public EnhancedCallback(Object delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
   {
      return method.invoke(this);
   }

   @Override
   public Object getDelegate()
   {
      return delegate;
   }
}
