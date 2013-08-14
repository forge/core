package org.jboss.forge.addon.ui;

import org.jboss.forge.furnace.spi.ListenerRegistration;

public interface UIProvider
{
   /**
    * Add a {@link CommandExecutionListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener);

}
