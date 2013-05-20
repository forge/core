package org.jboss.forge.furnace.dependencies;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.example.extension.TestExtension;
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
public class AddonCDIExtensionTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(TestExtension.class)
               .addBeansXML()
               .addAsServiceProvider(Extension.class, TestExtension.class);

      return archive;
   }

   @Inject
   private TestExtension extension;

   @Test
   public void testCDIExtensionsFunctionNormally() throws Exception
   {
      Assert.assertNotNull(extension);
      Assert.assertTrue(extension.isInvoked());
   }
}