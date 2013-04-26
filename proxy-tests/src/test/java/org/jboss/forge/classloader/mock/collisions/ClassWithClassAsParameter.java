/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock.collisions;

import org.jboss.forge.proxy.Proxies;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassWithClassAsParameter
{

   private Class<?> referenceType;

   public ClassWithClassAsParameter()
   {
      // this needs to be proxyable
   }

   public ClassWithClassAsParameter(Class<?> referenceType)
   {
      this.referenceType = referenceType;
   }

   public boolean verify(Class<?> type)
   {
      return (referenceType.isAssignableFrom(type));
   }

   public boolean isProxyType(Class<?> type)
   {
      return Proxies.isProxyType(type);
   }
}
