package org.jboss.forge.furnace.dependencies;

import javax.inject.Inject;

import org.example.NonService;
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
public class AddonSelfInjectionTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(NonService.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private NonService simple;

   @Test
   public void testContainerInjection()
   {
      Assert.assertNotNull(simple);
   }

   @Test
   public void testLifecycle() throws Exception
   {
      Assert.assertNotNull(simple);
      Assert.assertTrue(simple.isPerformObserved());
      Assert.assertFalse(simple.isPreShutdownObserved());
   }

}