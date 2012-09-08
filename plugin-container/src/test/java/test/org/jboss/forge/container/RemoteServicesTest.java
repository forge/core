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
public class RemoteServicesTest extends AbstractForgeTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractForgeTest.getDeployment().addAsServiceProvider(Extension.class, ContainerServiceExtension.class);
   }

   @Inject
   @Service
   private RemoteService remoteRemote;

   @Test
   public void testRemoteInjectionOfRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
   }

   @Test
   public void testRemoteInvocationOfRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
      remoteRemote.invoke();
   }

}