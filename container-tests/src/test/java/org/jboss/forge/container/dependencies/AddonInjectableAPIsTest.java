package org.jboss.forge.container.dependencies;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.ContainerControl;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.mocks.ServiceBean;
import org.jboss.forge.container.repositories.AddonRepository;
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
               .addClasses(ServiceBean.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRepository addonRepository;

   @Inject
   private AddonRegistry addonRegistry;

   @Inject
   private ContainerControl container;

   @Test
   public void testAPIsAreProducedAndInjectable() throws Exception
   {
      Assert.assertNotNull(addonRegistry);
      Assert.assertNotNull(container);
      Assert.assertNotNull(addonRepository);
   }
}