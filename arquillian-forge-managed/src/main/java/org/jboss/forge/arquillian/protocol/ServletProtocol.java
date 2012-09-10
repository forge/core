package org.jboss.forge.arquillian.protocol;

import java.util.Collection;

import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.test.spi.ContainerMethodExecutor;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.protocol.Protocol;
import org.jboss.arquillian.container.test.spi.command.CommandCallback;
import org.jboss.forge.arquillian.ForgeContainerMethodExecutor;
import org.jboss.forge.arquillian.ForgeDeploymentPackager;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServletProtocol implements Protocol<ServletProtocolConfiguration>
{

   @Override
   public Class<ServletProtocolConfiguration> getProtocolConfigurationClass()
   {
      return ServletProtocolConfiguration.class;
   }

   @Override
   public ProtocolDescription getDescription()
   {
      return new ServletProtocolDescription();
   }

   @Override
   public DeploymentPackager getPackager()
   {
      return new ForgeDeploymentPackager();
   }

   @Override
   public ContainerMethodExecutor getExecutor(ServletProtocolConfiguration protocolConfiguration,
            ProtocolMetaData metaData, CommandCallback callback)
   {
      Collection<HTTPContext> contexts = metaData.getContexts(HTTPContext.class);
      if (contexts.size() == 0)
      {
         throw new IllegalArgumentException(
                  "No " + HTTPContext.class.getName() + " found in " + ProtocolMetaData.class.getName() + ". " +
                           "Servlet protocol can not be used");
      }
      return new ForgeContainerMethodExecutor(protocolConfiguration, contexts, callback);
   }

}
