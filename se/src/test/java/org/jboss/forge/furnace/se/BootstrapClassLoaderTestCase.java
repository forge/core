package org.jboss.forge.furnace.se;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.FurnaceImpl;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.se.BootstrapClassLoader;
import org.jboss.forge.furnace.se.ForgeFactory;
import org.jboss.forge.furnace.util.AddonFilters;
import org.jboss.forge.proxy.ClassLoaderAdapterCallback;
import org.junit.Assert;
import org.junit.Test;

public class BootstrapClassLoaderTestCase
{
   @Test
   public void shouldBeAbleToLoadEnvironment() throws Exception
   {
      final BootstrapClassLoader cl = new BootstrapClassLoader("bootpath");
      Class<?> bootstrapType = cl.loadClass("org.jboss.forge.furnace.FurnaceImpl");
      Method method = bootstrapType.getMethod("startAsync", new Class<?>[] { ClassLoader.class });
      Object result = method.invoke(bootstrapType.newInstance(), cl);
      Assert.assertEquals(FurnaceImpl.class.getName(), result.getClass().getName());
   }

   @Test
   public void shouldBeAbleToUseFactoryDelegateTypesafely() throws Exception
   {
      Furnace instance = ForgeFactory.getInstance();
      Assert.assertNotNull(instance);
      AddonRegistry registry = instance.getAddonRegistry();
      Assert.assertNotNull(registry);
   }

   @Test
   public void shouldBeAbleToPassPrimitivesIntoDelegate() throws Exception
   {
      Furnace instance = ForgeFactory.getInstance();
      Assert.assertNotNull(instance);
      instance.setServerMode(false);
   }

   @Test
   public void shouldBeAbleToPassClassesIntoDelegate() throws Exception
   {
      Furnace instance = ForgeFactory.getInstance();
      File tempDir = File.createTempFile("test", "repository");
      tempDir.delete();
      tempDir.mkdir();
      tempDir.deleteOnExit();
      instance.addRepository(AddonRepositoryMode.IMMUTABLE, tempDir);
      instance.getRepositories().get(0).getAddonResources(AddonId.from("a", "1"));
   }

   @Test
   public void shouldBeAbleToPassInterfacesIntoDelegate() throws Exception
   {
      Furnace instance = ForgeFactory.getInstance();
      Set<Addon> addons = instance.getAddonRegistry().getAddons(AddonFilters.allStarted());
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
      Furnace instance = ForgeFactory.getInstance();
      ClassLoader fromLoader = AddonId.class.getClassLoader();
      ClassLoader toLoader = instance.getClass().getSuperclass().getClassLoader();
      ClassLoaderAdapterCallback.enhance(fromLoader, toLoader,
               AddonId.from("a", "1"), AddonId.class);
   }
}