package test.org.jboss.forge.container;

import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.forge.Root;
import org.jboss.forge.container.impl.ContainerBeanRegistrant;
import org.jboss.forge.container.impl.ContainerServiceExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RemoteServicesImproperInjectionTest
{
   @Deployment
   @ShouldThrowException(DeploymentException.class)
   public static JavaArchive getDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addAsServiceProvider(Extension.class, ContainerBeanRegistrant.class)
               .addAsServiceProvider(Extension.class, ContainerServiceExtension.class);
   }

   @Inject
   private RemoteService localRemote;

   @Test
   public void placeholder() throws Exception
   {
      // needed for JUnit to be happy and run the test class
   }
}