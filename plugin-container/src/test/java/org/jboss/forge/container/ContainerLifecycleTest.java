package org.jboss.forge.container;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.container.services.ContainerServiceManager;
import org.jboss.forge.test.AbstractForgeTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

public class ContainerLifecycleTest extends AbstractForgeTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractForgeTest.getDeployment().addAsServiceProvider(Extension.class, ContainerServiceManager.class);
   }

   @Inject
   private LifecycleEventObserver observer;

   @Test
   public void testContainerStartup()
   {
      Assert.assertTrue(observer.isObservedPostStartup());
   }
}