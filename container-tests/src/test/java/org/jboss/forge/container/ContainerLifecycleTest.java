package org.jboss.forge.container;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerLifecycleTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(ContainerLifecycleEventObserver.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private ContainerLifecycleEventObserver observer;

   @Test
   public void testContainerStartup()
   {
      Assert.assertTrue(observer.isObservedPostStartup());
   }
}