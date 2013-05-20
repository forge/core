package org.jboss.forge.furnace.deployment;

import javax.inject.Inject;

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
public class AddonMissingDelayedRequiredDependencyTest
{
   @Deployment(order = 1)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("dependency", "2")));

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 2)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(PublisherService.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private PublisherService published;

   @Test
   public void testInjection() throws Exception
   {
      Assert.assertNotNull(published);
      Assert.assertNotNull(published.getMessage());
   }

}