package org.jboss.forge.arquillian;

import java.io.File;
import java.util.Collection;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.archive.ForgeRemoteAddon;
import org.jboss.forge.arquillian.runner.CDIEnricherRemoteExtensionWorkaround;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class ForgeDeploymentPackager implements DeploymentPackager
{
   @Override
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors)
   {
      if (testDeployment.getApplicationArchive() instanceof ForgeArchive)
      {
         ForgeArchive deployment = ForgeArchive.class.cast(testDeployment.getApplicationArchive());

         deployment.addAsServiceProvider(RemoteLoadableExtension.class, CDIEnricherRemoteExtensionWorkaround.class);
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

   protected static File[] resolveDependencies(final String coords)
   {
      return Maven.resolver().loadPomFromFile("pom.xml")
               .resolve(coords)
               .withTransitivity().asFile();
   }
}
