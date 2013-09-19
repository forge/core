package org.jboss.forge.addon.ui;

import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * A {@link UIProvider} allows registering of {@link CommandExecutionListener} objects independently of the underlying
 * UI provider used (Shell, Eclipse, Idea)
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIProvider
{
   /**
    * Add a {@link CommandExecutionListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener);

   /**
    * Returns true if this {@link UIProvider} is running a Graphical User Interface.
    */
   public boolean isGUI();

}
