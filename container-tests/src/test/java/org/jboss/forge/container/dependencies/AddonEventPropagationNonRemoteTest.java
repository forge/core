package org.jboss.forge.container.dependencies;

import javax.inject.Inject;

import org.example.event.EventPayload2;
import org.example.event.EventResponseService;
import org.example.event.EventService;
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
public class AddonEventPropagationNonRemoteTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(EventService.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("dependency", "1")));

      return archive;
   }

   @Deployment(name = "dependency,1", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(EventResponseService.class, EventPayload2.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private EventService sender;

   @Test
   public void testNonRemoteEventPropagationDoesNotCrossContainers() throws Exception
   {
      Assert.assertFalse(sender.isLocalRequestRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
      Assert.assertFalse(sender.isRemoteResponseRecieved());
      sender.fireNonRemote();
      Assert.assertTrue(sender.isLocalRequestRecieved());
      Assert.assertFalse(sender.isRemoteResponseRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
   }

}