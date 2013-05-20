package org.jboss.forge.furnace;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.mocks.AbstractImplementation;
import org.jboss.forge.furnace.mocks.ExportedInterface;
import org.jboss.forge.furnace.mocks.ImplementingClass1;
import org.jboss.forge.furnace.mocks.ImplementingClass2;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerServiceInstanceTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(
                        AbstractImplementation.class,
                        ExportedInterface.class,
                        ImplementingClass1.class,
                        ImplementingClass2.class
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private Instance<ExportedInterface> instanceInterfaceInstance;

   @Test
   @Ignore
   public void testRegisteredServices()
   {
      Assert.assertNotNull(instanceInterfaceInstance.get());
      for (ExportedInterface instance : instanceInterfaceInstance)
      {
         Assert.assertNotNull(instance);
      }
      Instance<ImplementingClass1> implementation = instanceInterfaceInstance.select(ImplementingClass1.class);
      Assert.assertTrue(implementation.get() instanceof AbstractImplementation);
   }
}