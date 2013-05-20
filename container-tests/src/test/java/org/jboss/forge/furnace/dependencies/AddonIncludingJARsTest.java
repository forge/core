package org.jboss.forge.furnace.dependencies;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonStatus;
import org.jboss.forge.test.Tests;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonIncludingJARsTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsLibraries(Tests.resolveDependencies("javax.enterprise:cdi-api:1.0"))
               .addBeansXML();

      return archive;
   }

   @Inject
   private Addon self;

   @Test
   public void testContainerInjectionWorksWithConflictingCDIDependency()
   {
      Assert.assertNotNull(self);
      Assert.assertEquals(AddonStatus.STARTED, self.getStatus());
   }

}