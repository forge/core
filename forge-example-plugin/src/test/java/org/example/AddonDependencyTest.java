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
import org.jboss.forge.container.services.Service;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonDependencyTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(SimpleService.class, ConsumingService.class, TestExtension.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .setAsForgeXML(new StringAsset("<addon>" +
                        "<dependency " +
                        "name=\"dependency\" " +
                        "min-version=\"X\" " +
                        "max-version=\"Y\" " +
                        "optional=\"false\"/>" +
                        "</addon>"));

      return archive;
   }

   @Deployment(name = "dependency", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(PublishedService.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon/>"));

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
      Assert.assertEquals("I am ConsumingService. Remote service says [" + remote.getMessage() + "]",
               consuming.getMessage());
      Assert.assertNotSame(consuming, remote);
      Assert.assertNotSame(consuming.getClassLoader(), remote.getClassLoader());
   }

}