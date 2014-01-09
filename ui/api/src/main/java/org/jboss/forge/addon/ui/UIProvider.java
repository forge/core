package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * A {@link UIProvider} allows registering of {@link CommandExecutionListener} objects independently of the underlying
 * UI provider used (Shell, Eclipse, Idea)
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIProvider
{
   /**
    * Returns true if this {@link UIProvider} is running a Graphical User Interface.
    */
   boolean isGUI();

   /**
    * Returns the output object used to display messages during a UI interation
    */
   UIOutput getOutput();
}