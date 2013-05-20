package org.jboss.forge.furnace.dependencies;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.mocks.ServiceBean;
import org.jboss.forge.furnace.mocks.ServiceInterface;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonInjectableAPIsTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(ServiceBean.class, ServiceInterface.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRepository addonRepository;

   @Inject
   private AddonRegistry addonRegistry;

   @Inject
   private Addon self;

   @Inject
   private Furnace forge;

   @Test
   public void testAPIsAreProducedAndInjectable() throws Exception
   {
      Assert.assertNotNull(addonRegistry);
      Assert.assertNotNull(self);
      Assert.assertNotNull(forge);
      Assert.assertNotNull(addonRepository);
   }
}