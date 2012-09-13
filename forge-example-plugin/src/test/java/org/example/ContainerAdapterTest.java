package org.example;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

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
public class ContainerAdapterTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(SimpleService.class, ConsumingService.class, TestExtension.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .setAsForgeXML(new StringAsset("<addon><dependency " +
                        "name=\"3d09722c-6e71-4bac-b315-e8078217dc98\" " +
                        "min-version=\"X\" " +
                        "max-version=\"Y\" " +
                        "optional=\"false\"/></addon>"));

      return archive;
   }

   @Inject
   private SimpleService simple;

   @Inject
   private TestExtension extension;

   @Test
   public void testContainerInjection()
   {
      Assert.assertNotNull(simple);
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