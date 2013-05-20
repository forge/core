package org.jboss.forge.furnace.deployment;

import javax.inject.Inject;

import org.example.LifecycleListenerService;
import org.example.PublisherService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.util.AddonFilters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonMultipleDependencyVersionTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("dep", "1")),
                        AddonDependencyEntry.create(AddonId.from("dep", "2"))
               );

      return archive;
   }

   @Deployment(name = "dep,1", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(LifecycleListenerService.class)
               .addBeansXML();

      return archive;
   }

   @Deployment(name = "dep,2", testable = false, order = 3)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(LifecycleListenerService.class, PublisherService.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testVersionLookup() throws Exception
   {
      int count = 0;
      for (Addon addon : registry.getAddons(AddonFilters.allStarted()))
      {
         for (Class<?> service : addon.getServiceRegistry().getExportedTypes())
         {
            if (service.getName().equals(LifecycleListenerService.class.getName()))
            {
               ExportedInstance<?> instance = addon.getServiceRegistry().getExportedInstance(service);
               Object serviceInstance = instance.get();
               Assert.assertNotNull(serviceInstance);
               Object result = serviceInstance.getClass().getMethod("isPerformObserved").invoke(serviceInstance);
               Assert.assertTrue((Boolean) result);
               count++;
            }
         }
      }
      Assert.assertEquals(2, count);
   }
}