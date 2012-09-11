package org.example;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
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
               .addClasses(SimpleService.class, ConsumingService.class)
               .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private SimpleService service;

   @Test
   public void testContainerStartup()
   {
      Assert.assertNotNull(service);
   }
}