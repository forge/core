package org.jboss.forge.se.init;

import org.jboss.forge.container.Forge;

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
