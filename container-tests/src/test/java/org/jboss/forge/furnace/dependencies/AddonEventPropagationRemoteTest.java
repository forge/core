package org.jboss.forge.furnace.dependencies;

import javax.inject.Inject;

import org.example.event.EventPayload1;
import org.example.event.EventPayload3;
import org.example.event.EventResponseService;
import org.example.event.EventService;
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
public class AddonEventPropagationRemoteTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(EventService.class, EventPayload1.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create(AddonId.from("dependencyA", "1")));

      return archive;
   }

   @Deployment(name = "dependencyA,1", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependencyA.jar")
               .addClasses(EventResponseService.class, EventPayload3.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private EventService sender;

   @Test
   public void testEventPropagationAcrossContainers() throws Exception
   {
      Assert.assertFalse(sender.isLocalRequestRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
      Assert.assertFalse(sender.isRemoteResponseRecieved());
      sender.fire();
      Assert.assertTrue(sender.isLocalRequestRecieved());
      Assert.assertTrue(sender.isRemoteResponseRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
   }

}