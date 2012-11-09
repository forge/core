package org.example;

import javax.inject.Inject;

import org.example.published.PublishedService;
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
public class AddonSelfInjectionExposedServiceTest
{
   @Deployment
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PublishedService.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon/>"));

      return archive;
   }

   @Inject
   private PublishedService local;

   @Inject
   @Service
   private PublishedService remote;

   @Test
   public void testLocalServiceIsInjected() throws Exception
   {
      Assert.assertNotNull(remote);
   }

   @Test
   public void testLocalServiceCanBeInvoked() throws Exception
   {
      Assert.assertNotNull(remote);
      Assert.assertEquals("I am PublishedService.", remote.getMessage());
   }

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