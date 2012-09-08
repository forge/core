package test.org.jboss.forge.container;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.container.services.ContainerServiceExtension;
import org.jboss.forge.test.AbstractForgeTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class LocalServicesTest extends AbstractForgeTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractForgeTest.getDeployment().addAsServiceProvider(Extension.class, ContainerServiceExtension.class)
               .addClasses(LocalService.class);
   }

   @Inject
   private LocalService localLocal;

   @Test
   public void testLocalInjectionOfLocalService()
   {
      Assert.assertNotNull(localLocal);
   }

   @Test
   public void testLocalInvocationOfLocalService()
   {
      Assert.assertNotNull(localLocal);
      localLocal.invoke();
   }
}