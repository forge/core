package org.example;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
   public static JavaArchive getDeployment()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
               .addClasses(SimpleService.class, ConsumingService.class, TestExtension.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class);

      System.out.println(archive.toString(true));

      return archive;
   }

   @Inject
   private SimpleService service;

   @Inject
   private TestExtension extension;

   @Test
   public void testContainerInjection()
   {
      Assert.assertNotNull(service);
   }

   @Test
   public void testLifecycle() throws Exception
   {
      Assert.assertTrue(service.isStartupObserved());
      Assert.assertTrue(service.isPostStartupObserved());
      Assert.assertFalse(service.isPreShutdownObserved());
      Assert.assertFalse(service.isShutdownObserved());
   }

   @Test
   public void testCDIExtensionsFunctionNormally() throws Exception
   {
      Assert.assertTrue(extension.isInvoked());
   }
}