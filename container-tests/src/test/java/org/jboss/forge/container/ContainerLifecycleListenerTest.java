package org.jboss.forge.container;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.spi.ContainerLifecycleListener;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerLifecycleListenerTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(ContainerLifecycleListenerTest.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private Forge forge;

   @Test
   public void testContainerStartup()
   {
      ForgeImpl impl = (ForgeImpl) forge;
      List<ContainerLifecycleListener> listeners = impl.getRegisteredListeners();
      Assert.assertEquals(1, listeners.size());
      Assert.assertEquals(1, ((TestLifecycleListener) listeners.get(0)).beforeStartTimesCalled);
   }
}