package org.jboss.forge.furnace.hotswap;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonOptionalDependencyHotSwapTest
{
   @Deployment(order = 3)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML();

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("dep", "2"), false, true));

      return archive;
   }

   @Deployment(name = "dep,2", testable = false, order = 1)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private AddonRepository repository;

   @Test
   public void testHotSwap() throws Exception
   {
      AddonId depOneId = AddonId.from("dep", "1");
      AddonId depTwoId = AddonId.from("dep", "2");

      Addon depOne = registry.getAddon(depOneId);
      Addon depTwo = registry.getAddon(depTwoId);

      ClassLoader depOneClassloader = depOne.getClassLoader();
      ClassLoader depTwoClassloader = depTwo.getClassLoader();

      ((MutableAddonRepository) depTwo.getRepository()).disable(depTwoId);
      Addons.waitUntilStopped(depTwo, 10, TimeUnit.SECONDS);
      Addons.waitUntilStarted(depOne, 10, TimeUnit.SECONDS);

      Assert.assertNotNull(depOne.getClassLoader());
      Assert.assertNotEquals(depOneClassloader, depOne.getClassLoader());
      depOneClassloader = depOne.getClassLoader();

      ((MutableAddonRepository) repository).enable(depTwoId);
      Addons.waitUntilStarted(depTwo, 10, TimeUnit.SECONDS);
      Thread.sleep(1000);

      Assert.assertNotEquals(depOneClassloader, depOne.getClassLoader());
      Assert.assertNotEquals(depOneClassloader.toString(), depOne.getClassLoader().toString());
      Assert.assertNotEquals(depTwoClassloader, depTwo.getClassLoader());
      Assert.assertNotEquals(depTwoClassloader.toString(), depTwo.getClassLoader().toString());
   }

}