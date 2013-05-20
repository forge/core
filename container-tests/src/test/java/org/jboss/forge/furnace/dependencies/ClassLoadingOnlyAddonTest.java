package org.jboss.forge.furnace.dependencies;

import org.example.NonService;
import org.example.PublisherService;
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
public class ClassLoadingOnlyAddonTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(PublisherService.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("noncdi", "1")));

      return archive;
   }

   @Deployment(testable = false, name = "noncdi,1", order = 2)
   public static ForgeArchive getDeployment2()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(NonService.class);

      return archive;
   }

   @Test
   public void testClassesLoadedNormallyFromDependencyAddons() throws Exception
   {
      Assert.assertEquals("NonService", NonService.class.getSimpleName());
   }
}