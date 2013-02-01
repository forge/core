/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.se.init;

import org.jboss.forge.container.Forge;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;

public class ForgeFactory
{
   public static Forge getInstance()
   {
      try
      {
         final BootstrapClassLoader cl = new BootstrapClassLoader("bootpath");
         Class<?> bootstrapType = cl.loadClass("org.jboss.forge.container.ForgeImpl");
         return (Forge) ClassLoaderAdapterCallback.enhance(ForgeFactory.class.getClassLoader(), cl,
                  bootstrapType.newInstance(), Forge.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
