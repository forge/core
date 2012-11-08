package test.org.jboss.forge.container;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.container.impl.ContainerBeanRegistrant;
import org.jboss.forge.container.impl.ContainerServiceExtension;
import org.jboss.forge.test.AbstractForgeTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ContainerLifecycleTest extends AbstractForgeTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractForgeTest.getDeployment()
               .addAsServiceProvider(Extension.class, ContainerServiceExtension.class)
               .addAsServiceProvider(Extension.class, ContainerBeanRegistrant.class)
               .addClasses(ContainerLifecycleEventObserver.class);
   }

   @Inject
   private ContainerLifecycleEventObserver observer;

   @Test
   public void testContainerStartup()
   {
      Assert.assertTrue(observer.isObservedPostStartup());
   }
}