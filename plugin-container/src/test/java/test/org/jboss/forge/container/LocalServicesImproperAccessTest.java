package test.org.jboss.forge.container;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.container.services.ContainerServiceExtension;
import org.jboss.forge.container.services.Service;
import org.jboss.forge.test.AbstractForgeTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class LocalServicesImproperAccessTest extends AbstractForgeTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractForgeTest.getDeployment().addAsServiceProvider(Extension.class, ContainerServiceExtension.class);
   }

   @Inject
   @Service
   private LocalService remoteLocal;

   @Test
   public void testRemoteInjectionOfLocalService()
   {
      Assert.assertNotNull(remoteLocal);
   }

   @Test(expected = IllegalStateException.class)
   public void testRemoteInvocationOfLocalServiceFails()
   {
      Assert.assertNotNull(remoteLocal);
      remoteLocal.invoke();
   }
}