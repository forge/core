package org.jboss.forge.aesh.commands;

import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.base.UICommandMetadataBase;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ExitCommand extends BaseExitCommand
{
   @Override
   public UICommandMetadata getMetadata()
   {
      return new UICommandMetadataBase("exit", "Exit the shell");
   }
}
