package org.jboss.forge.arquillian.runner;

import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ServletLoadableExtension implements LoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.service(CommandService.class, ServletCommandService.class);
   }
}
