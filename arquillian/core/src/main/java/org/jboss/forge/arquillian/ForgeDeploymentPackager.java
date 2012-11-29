package org.jboss.forge.arquillian;

import java.io.File;
import java.util.Collection;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.archive.ForgeArchiveImpl;
import org.jboss.forge.arquillian.runner.BeanManagerProducer;
import org.jboss.forge.arquillian.runner.CDIEnricherRemoteExtensionWorkaround;
import org.jboss.forge.arquillian.runner.ServletTestRunner;
import org.jboss.forge.arquillian.runner.ServletTestServer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class ForgeDeploymentPackager implements DeploymentPackager
{
   @Override
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors)
   {
      if (!(testDeployment.getApplicationArchive() instanceof ForgeArchive))
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");

      ForgeArchive deployment = ForgeArchive.class.cast(testDeployment.getApplicationArchive());

      deployment.addClasses(ServletTestServer.class, ServletTestRunner.class, BeanManagerProducer.class,
               CDIEnricherRemoteExtensionWorkaround.class);
      deployment.addAsServiceProvider(RemoteLoadableExtension.class, CDIEnricherRemoteExtensionWorkaround.class);
      deployment.addAsLibraries(testDeployment.getAuxiliaryArchives());
      deployment.addAsLibraries(resolveDependencies("org.jboss.shrinkwrap:shrinkwrap-impl-base:1.0.1"));
      deployment.addAsLibraries(resolveDependencies("org.eclipse.jetty:jetty-server:8.1.5.v20120716"));
      deployment.addAsLibraries(resolveDependencies("org.eclipse.jetty:jetty-servlet:8.1.5.v20120716"));

      deployment.addClasses(ForgeArchive.class, ForgeArchiveImpl.class);

      return deployment;
   }

   protected static File[] resolveDependencies(final String coords)
   {
      return Maven.resolver().loadPomFromFile("pom.xml")
               .resolve(coords)
               .withTransitivity().asFile();
   }
}
