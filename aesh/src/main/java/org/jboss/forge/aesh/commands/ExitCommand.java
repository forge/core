package org.jboss.forge.aesh.commands;

import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ExitCommand extends BaseExitCommand
{
   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("exit").description("Exit the shell");
   }
}
