package org.jboss.forge.arquillian.runner;

import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.forge.arquillian.ForgeDeploymentScenarioGenerator;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServletLoadableExtension implements LoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.service(CommandService.class, ServletCommandService.class);
      builder.service(DeploymentScenarioGenerator.class, ForgeDeploymentScenarioGenerator.class);
   }
}
