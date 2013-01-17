package org.jboss.forge.se.init;

import java.lang.reflect.Method;
import java.util.Set;

import org.jboss.forge.classloader.ClassLoaderAdapterCallback;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonFilters;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeImpl;
import org.junit.Assert;
import org.junit.Test;

public class BootstrapClassLoaderTestCase
{
   @Test
   public void shouldBeAbleToLoadEnvironment() throws Exception
   {
      final BootstrapClassLoader cl = new BootstrapClassLoader("bootpath");
      Class<?> bootstrapType = cl.loadClass("org.jboss.forge.container.ForgeImpl");
      Method method = bootstrapType.getMethod("startAsync", new Class<?>[] { ClassLoader.class });
      Object result = method.invoke(bootstrapType.newInstance(), cl);
      Assert.assertEquals(ForgeImpl.class.getName(), result.getClass().getName());
   }

   @Test
   public void shouldBeAbleToUseFactoryDelegateTypesafely() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      Assert.assertNotNull(instance);
      AddonRegistry registry = instance.getAddonRegistry();
      Assert.assertNotNull(registry);
   }

   @Test
   public void shouldBeAbleToPassPrimitivesIntoDelegate() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      Assert.assertNotNull(instance);
      instance.setServerMode(false);
   }

   @Test
   public void shouldBeAbleToPassClassesIntoDelegate() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      instance.getRepository().getAddonResources(AddonId.from("a", "1"));
   }

   @Test
   public void shouldBeAbleToPassInterfacesIntoDelegate() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      Set<Addon> addons = instance.getAddonRegistry().getRegisteredAddons(AddonFilters.allWaiting());
      Assert.assertNotNull(addons);
   }

   @Test
   public void shouldBeAbleToEnhanceAddonId() throws Exception
   {
      ClassLoader loader = AddonId.class.getClassLoader();
      AddonId enhanced = ClassLoaderAdapterCallback.enhance(loader, loader, AddonId.from("a", "1"), AddonId.class);
      Assert.assertNotNull(enhanced);

   }

   @Test
   public void shouldBeAbleToEnhanceAddonIdIntoDelegate() throws Exception
   {
      Forge instance = ForgeFactory.getInstance();
      ClassLoader fromLoader = AddonId.class.getClassLoader();
      ClassLoader toLoader = instance.getClass().getSuperclass().getClassLoader();
      ClassLoaderAdapterCallback.enhance(fromLoader, toLoader,
               AddonId.from("a", "1"), AddonId.class);
   }
}