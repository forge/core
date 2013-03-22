package org.jboss.forge.container;

import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.mocks.ServiceBean;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ExportedServicesMissingTest
{
   @Deployment
   @ShouldThrowException(DeploymentException.class)
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private ServiceBean remote;

   @Test
   public void testRemoteInjectionOfRemoteService()
   {
      Assert.fail("Should not have deployed");
   }

}