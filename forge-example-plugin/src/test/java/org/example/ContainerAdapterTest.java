package org.example;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.services.Service;
import org.jboss.forge.test.AbstractForgeTest;
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
public class ContainerAdapterTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(SimpleService.class, ConsumingService.class, TestExtension.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .setAsForgeXML(new StringAsset("<addon><dependency " +
                        "name=\"dependency\" " +
                        "min-version=\"X\" " +
                        "max-version=\"Y\" " +
                        "optional=\"false\"/></addon>"));

      return archive;
   }

   @Deployment(name = "dependency", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency")
               .addAsLibraries(AbstractForgeTest.resolveDependencies("org.jboss.forge:forge-example-plugin-2"))
               .setAsForgeXML(new StringAsset("<addon/>"));

      return archive;
   }

   @Inject
   private SimpleService simple;

   @Inject
   private ConsumingService consuming;

   @Inject
   private TestExtension extension;

   @Test
   public void testContainerInjection()
   {
      Assert.assertNotNull(simple);
   }

   @Inject
   @Service
   PublishedService remote;

   @Test
   public void testRemoteServiceInjection() throws Exception
   {
      Assert.assertEquals("I am ConsumingService. Remote service says [I am PublishedService.]", consuming.getMessage());
      Assert.assertNotSame(consuming, remote);
      Assert.assertNotSame(consuming.getClassLoader(), remote.getClassLoader());
   }

   @Test
   public void testLifecycle() throws Exception
   {
      Assert.assertTrue(simple.isStartupObserved());
      Assert.assertTrue(simple.isPostStartupObserved());
      Assert.assertFalse(simple.isPreShutdownObserved());
      Assert.assertFalse(simple.isShutdownObserved());
   }

   @Test
   public void testCDIExtensionsFunctionNormally() throws Exception
   {
      Assert.assertTrue(extension.isInvoked());
   }
}