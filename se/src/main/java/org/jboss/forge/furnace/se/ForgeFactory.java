/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.se;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;

public class ForgeFactory
{
   public static Furnace getInstance()
   {
      try
      {
         final BootstrapClassLoader loader = new BootstrapClassLoader("bootpath");
         Class<?> bootstrapType = loader.loadClass("org.jboss.forge.furnace.FurnaceImpl");
         return (Furnace) ClassLoaderAdapterCallback.enhance(ForgeFactory.class.getClassLoader(), loader,
                  bootstrapType.newInstance(), Furnace.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static Furnace getInstance(ClassLoader loader)
   {
      try
      {
         Class<?> bootstrapType = loader.loadClass("org.jboss.forge.furnace.FurnaceImpl");
         return (Furnace) ClassLoaderAdapterCallback.enhance(ForgeFactory.class.getClassLoader(), loader,
                  bootstrapType.newInstance(), Furnace.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
