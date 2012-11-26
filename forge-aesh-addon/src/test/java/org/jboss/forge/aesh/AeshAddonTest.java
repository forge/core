package org.jboss.forge.aesh;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AeshAddonTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(AeshShell.class)
               .addAsLibraries(
                        Maven.resolver().loadPomFromFile("pom.xml").resolve("org.jboss.aesh:aesh:0.22")
                                 .withTransitivity().as(JavaArchive.class))
               .addAsManifestResource(new StringAsset(""), ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon/>"));

      return archive;
   }

   @Inject
   private AeshShell simple;

   @Test
   public void testContainerInjection()
   {
      Assert.assertNotNull(simple);
   }

   @Test
   public void testLifecycle() throws Exception
   {

   }

}