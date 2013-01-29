package org.jboss.forge.container.dependencies;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonHotSwapTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml")
               );

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("dep", "2")));

      return archive;
   }

   @Deployment(name = "dep,2", testable = false, order = 3)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private AddonRepository repository;

   @Test
   public void testHotSwap() throws Exception
   {
      AddonId d1id = AddonId.from("dep", "1");
      AddonId d2id = AddonId.from("dep", "2");

      Addon d1 = registry.getRegisteredAddon(d1id);
      Addon d2 = registry.getRegisteredAddon(d2id);

      ClassLoader d1cl = d1.getClassLoader();
      ClassLoader d2cl = d2.getClassLoader();

      repository.disable(d2id);
      registry.stop(d2);

      repository.enable(d2id);
      Future<?> future = registry.start(d2);
      future.get(10, TimeUnit.SECONDS); // shouldn't take this long

      Assert.assertNotEquals(d1cl, registry.getRegisteredAddon(d1id).getClassLoader());
      Assert.assertNotEquals(d1cl.toString(), registry.getRegisteredAddon(d1id).getClassLoader().toString());
      Assert.assertNotEquals(d2cl, registry.getRegisteredAddon(d2id).getClassLoader());
      Assert.assertNotEquals(d2cl.toString(), registry.getRegisteredAddon(d2id).getClassLoader().toString());
   }

}