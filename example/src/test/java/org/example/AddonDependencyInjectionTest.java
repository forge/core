package org.example;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.example.consuming.ConsumingService;
import org.example.extension.TestExtension;
import org.example.published.PublishedService;
import org.example.simple.SimpleService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.services.Service;
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
public class AddonDependencyInjectionTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(SimpleService.class, ConsumingService.class, TestExtension.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("dependency", "2")));

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(PublishedService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private ConsumingService consuming;

   @Inject
   @Service
   private PublishedService remote;

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