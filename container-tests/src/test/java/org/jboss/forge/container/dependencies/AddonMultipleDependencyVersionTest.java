package org.jboss.forge.container.dependencies;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.example.LifecycleListenerService;
import org.example.PublisherService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
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
                        AddonDependency.create(AddonId.from("dep", "1")),
                        AddonDependency.create(AddonId.from("dep", "2"))
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
      Map<Addon, ServiceRegistry> services = registry.getServiceRegistries();
      int count = 0;
      for (Entry<Addon, ServiceRegistry> entry : services.entrySet())
      {
         for (Class<?> service : entry.getValue().getServices())
         {
            if (service.getName().equals(LifecycleListenerService.class.getName()))
            {
               ExportedInstance<?> instance = entry.getValue().getExportedInstance(service);
               Object serviceInstance = instance.get();
               Assert.assertNotNull(serviceInstance);
               Object result = serviceInstance.getClass().getMethod("isStartupObserved").invoke(serviceInstance);
               Assert.assertTrue((Boolean) result);
               count++;
            }
         }
      }
      Assert.assertEquals(2, count);
   }

}