package org.jboss.forge.se.init;

import java.lang.reflect.Method;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.Forge;
import org.junit.Assert;
import org.junit.Test;

public class BootstrapClassLoaderTestCase
{
   @Test
   public void shouldBeAbleToLoadEnvironment() throws Exception
   {
      final BootstrapClassLoader cl = new BootstrapClassLoader("bootpath");
      Class<?> bootstrapType = cl.loadClass("org.jboss.forge.container.Forge");
      Method method = bootstrapType.getMethod("startAsync", new Class<?>[] { ClassLoader.class });
      Object result = method.invoke(bootstrapType.newInstance(), cl);
      Assert.assertEquals(Forge.class.getName(), result.getClass().getName());
   }

   @Test
   public void shouldBeAbleToUseFactoryDelegateTypesafely() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      Assert.assertNotNull(instance);
      AddonRegistry registry = instance.getAddonRegistry();
      Assert.assertNotNull(registry);
   }
}