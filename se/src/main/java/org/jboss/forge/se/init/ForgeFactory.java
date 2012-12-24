package org.jboss.forge.se.init;

import net.sf.cglib.proxy.Enhancer;

import org.jboss.forge.container.Forge;

public class ForgeFactory
{
   public static Forge getInstance()
   {
      try
      {
         final BootstrapClassLoader cl = new BootstrapClassLoader("bootpath");
         Class<?> bootstrapType = cl.loadClass("org.jboss.forge.container.ForgeImpl");
         return (Forge) Enhancer.create(Forge.class, new ClassLoaderAdapterCallback(cl, bootstrapType.newInstance()));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
