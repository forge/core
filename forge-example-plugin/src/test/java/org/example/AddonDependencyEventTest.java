package org.example;

import javax.inject.Inject;

import org.example.event.EventResponseService;
import org.example.event.EventService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
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
public class AddonDependencyEventTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(EventService.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon>" +
                        "<dependency " +
                        "name=\"dependency\" " +
                        "min-version=\"X\" " +
                        "max-version=\"Y\" " +
                        "optional=\"false\"/>" +
                        "</addon>"));

      return archive;
   }

   @Deployment(name = "dependency", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClasses(EventResponseService.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon/>"));

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