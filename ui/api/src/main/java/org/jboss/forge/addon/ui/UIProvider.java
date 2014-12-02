package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * A {@link UIProvider} provides access to the underlying UI implementation.
 * 
 * Information like, if it is running under a GUI, as well as the out/err streams are available from implementations of
 * this interface
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
    * Returns the output object used to display messages during a UI interaction
    */
   UIOutput getOutput();

   /**
    * Returns the desktop associated with this UI.
    */
   UIDesktop getDesktop();
}