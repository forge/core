package org.jboss.forge.arquillian;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.archive.ForgeRemoteAddon;
import org.jboss.shrinkwrap.api.Archive;

public class ForgeDeploymentPackager implements DeploymentPackager
{
   @Override
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors)
   {
      if (testDeployment.getApplicationArchive() instanceof ForgeArchive)
      {
         ForgeArchive deployment = ForgeArchive.class.cast(testDeployment.getApplicationArchive());

         deployment.addAsLibraries(testDeployment.getAuxiliaryArchives());
         deployment.addClasses(ForgeArchive.class);

         return deployment;
      }
      else if (testDeployment.getApplicationArchive() instanceof ForgeRemoteAddon)
      {
         return testDeployment.getApplicationArchive();
      }
      else
      {
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");
      }
   }
}
