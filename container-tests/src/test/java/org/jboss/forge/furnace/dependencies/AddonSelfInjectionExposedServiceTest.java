package org.jboss.forge.furnace.dependencies;

import javax.inject.Inject;

import org.example.PublisherService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonSelfInjectionExposedServiceTest
{
   @Deployment
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PublisherService.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private PublisherService local;

   @Test
   public void testLocalServiceIsInjectedUnqualified() throws Exception
   {
      Assert.assertNotNull(local);
   }

   @Test
   public void testLocalServiceCanBeInvokedUnqualified() throws Exception
   {
      Assert.assertNotNull(local);
      Assert.assertEquals("I am PublishedService.", local.getMessage());
   }
}