/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
public class ClassWithGetterAndSetter
{
   private ClassWithPassthroughMethod passthrough;

   public ClassWithPassthroughMethod getPassthrough()
   {
      return passthrough;
   }

   public void setPassthrough(ClassWithPassthroughMethod passthrough)
   {
      this.passthrough = passthrough;
   }

   public boolean assertPassthroughNotProxied()
   {
      return Proxies.isForgeProxy(passthrough);
   }
}
