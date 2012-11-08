package test.org.jboss.forge.container;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.container.impl.ContainerBeanRegistrant;
import org.jboss.forge.container.impl.ContainerServiceExtension;
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
      return AbstractForgeTest.getDeployment()
               .addAsServiceProvider(Extension.class, ContainerBeanRegistrant.class)
               .addAsServiceProvider(Extension.class, ContainerServiceExtension.class);
   }

   @Inject
   @Service
   private RemoteService remoteRemote;

   @Test
   public void testRemoteInjectionOfRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
   }

   @Test(expected = IllegalStateException.class)
   public void testRemoteInvocationOfUnregisteredRemoteService()
   {
      Assert.assertNotNull(remoteRemote);
      remoteRemote.invoke();
   }

}