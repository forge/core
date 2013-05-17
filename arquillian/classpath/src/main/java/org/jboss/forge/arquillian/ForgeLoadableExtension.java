package org.jboss.forge.arquillian;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.container.test.spi.client.protocol.Protocol;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.forge.arquillian.protocol.ForgeProtocol;

public class ForgeLoadableExtension implements LoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.service(DeployableContainer.class, ForgeDeployableContainer.class);
      builder.service(DeploymentScenarioGenerator.class, ForgeDeploymentScenarioGenerator.class);
      builder.service(Protocol.class, ForgeProtocol.class);
      builder.service(AuxiliaryArchiveProcessor.class, ForgeAuxiliaryArchiveProcessor.class);
   }
}
