package org.jboss.forge.addon.shell.commands;

import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ExitCommand extends AbstractExitCommand
{
   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("exit").description("Exit the shell");
   }
}
