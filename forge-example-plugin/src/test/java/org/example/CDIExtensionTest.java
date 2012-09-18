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
public class CDIExtensionTest
{
   @Deployment(order = 2)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(TestExtension.class)
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .setAsForgeXML(new StringAsset("<addon/>"));

      return archive;
   }

   @Inject
   private TestExtension extension;

   @Test
   public void testCDIExtensionsFunctionNormally() throws Exception
   {
      Assert.assertTrue(extension.isInvoked());
   }
}