package org.jboss.forge.arquillian;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.forge.arquillian.runner.BeanManagerProducer;
import org.jboss.forge.arquillian.runner.CDIEnricherRemoteExtensionWorkaround;
import org.jboss.forge.arquillian.runner.ServletLoadableExtension;
import org.jboss.forge.arquillian.runner.ServletTestRunner;
import org.jboss.forge.arquillian.runner.ServletTestServer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class ForgeDeploymentPackager implements DeploymentPackager
{
   @Override
   public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> processors)
   {
      if (!(testDeployment.getApplicationArchive() instanceof JavaArchive))
         throw new IllegalStateException("Cannot deploy non JavaArchive.");

      JavaArchive deployment = JavaArchive.class.cast(testDeployment.getApplicationArchive());
      deployment.addClasses(ServletTestServer.class, ServletTestRunner.class, BeanManagerProducer.class,
               CDIEnricherRemoteExtensionWorkaround.class);
      deployment.addAsServiceProvider(LoadableExtension.class, ServletLoadableExtension.class);
      deployment.addAsServiceProvider(RemoteLoadableExtension.class, CDIEnricherRemoteExtensionWorkaround.class);

      WebArchive container = ShrinkWrap.create(WebArchive.class);
      container.addAsLibraries(deployment);
      container.addAsLibraries(testDeployment.getAuxiliaryArchives());
      container.addAsLibraries(resolveDependencies("org.eclipse.jetty:jetty-server:8.1.5.v20120716"));
      container.addAsLibraries(resolveDependencies("org.eclipse.jetty:jetty-servlet:8.1.5.v20120716"));

      return container;
   }

   protected static Collection<GenericArchive> resolveDependencies(final String coords)
   {
      return DependencyResolvers.use(MavenDependencyResolver.class)
               .loadMetadataFromPom("pom.xml")
               .artifacts(coords)
               .resolveAs(GenericArchive.class);
   }
}
