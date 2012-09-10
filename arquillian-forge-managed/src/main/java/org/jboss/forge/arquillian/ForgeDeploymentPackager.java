package org.jboss.forge.arquillian;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.forge.arquillian.runner.RemoteTestServer;
import org.jboss.forge.arquillian.runner.ServletLoadableExtension;
import org.jboss.forge.arquillian.runner.ServletTestRunner;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class ForgeDeploymentPackager implements DeploymentPackager
{
   @Override
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors)
   {
      if (!(testDeployment.getApplicationArchive() instanceof JavaArchive))
         throw new IllegalStateException("Cannot deploy non JavaArchive.");

      JavaArchive deployment = JavaArchive.class.cast(testDeployment.getApplicationArchive());
      deployment.addClasses(RemoteTestServer.class, ServletTestRunner.class);
      deployment.addAsServiceProvider(LoadableExtension.class, ServletLoadableExtension.class);

      // won't be added to the @Deployment unless I add them, and they won't be written to disk unless I write them
      Collection<Archive<?>> auxiliaryArchives = testDeployment.getAuxiliaryArchives();

      return deployment;
   }
}
