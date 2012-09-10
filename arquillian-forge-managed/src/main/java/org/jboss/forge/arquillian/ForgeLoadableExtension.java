package org.jboss.forge.arquillian;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.test.spi.client.protocol.Protocol;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.forge.arquillian.protocol.ServletProtocol;

public class ForgeLoadableExtension implements LoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.service(DeployableContainer.class, ForgeDeployableContainer.class);
      builder.service(Protocol.class, ServletProtocol.class);
   }
}
