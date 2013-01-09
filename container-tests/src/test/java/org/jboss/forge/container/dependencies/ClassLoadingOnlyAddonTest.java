package org.jboss.forge.container.dependencies;

import org.example.published.PublishedService;
import org.example.simple.SimpleService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
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
public class ClassLoadingOnlyAddonTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(PublishedService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("noncdi", "1")));

      return archive;
   }

   @Deployment(testable = false, name = "noncdi,1", order = 2)
   public static ForgeArchive getDeployment2()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(SimpleService.class);

      return archive;
   }

   @Test
   public void testCDIExtensionsFunctionNormally() throws Exception
   {
      Assert.assertEquals("SimpleService", SimpleService.class.getSimpleName());
   }
}