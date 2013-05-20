package org.jboss.forge.furnace.dependencies;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.example.ConsumingService;
import org.example.PublisherService;
import org.example.extension.TestExtension;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonDependencyInjectionTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ConsumingService.class, TestExtension.class)
               .addBeansXML()
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("dependency", "2")));

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(PublisherService.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private ConsumingService consuming;

   @Inject
   private PublisherService remote;

   @Test
   public void testRemoteServiceInjection() throws Exception
   {
      Assert.assertNotNull(consuming);
      Assert.assertNotNull(remote);
      Assert.assertEquals("I am ConsumingService. Remote service says [I am PublishedService.]",
               consuming.getMessage());
      Assert.assertEquals(remote.hashCode(), consuming.getRemoteHashCode());
      Assert.assertNotSame(consuming, remote);
      Assert.assertNotSame(consuming.getClassLoader(), remote.getClassLoader());
   }

}