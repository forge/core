package org.jboss.forge.container;
import javax.inject.Inject;

import org.jboss.forge.test.AbstractForgeTest;
import org.junit.Assert;
import org.junit.Test;


public class ContainerLifecycleTest extends AbstractForgeTest
{
   @Inject
   private LifecycleEventObserver observer;
   
   @Test
   public void test()
   {
      Assert.assertTrue(observer.isObservedPostStartup());
   }
}