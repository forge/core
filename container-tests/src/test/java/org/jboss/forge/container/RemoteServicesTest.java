package org.jboss.forge.container;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
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
public class RemoteServicesTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(LocalService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   @Service
   private RemoteService remoteRemote;

   @Test
   public void testRemoteInjectionOfRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
   }

   @Test(expected = IllegalStateException.class)
   public void testRemoteInvocationOfUnregisteredRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
      remoteRemote.invoke();
   }

}