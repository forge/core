/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
    * @return a human-friendly name of this {@link UIProvider}
    */
   String getName();

   /**
    * @return <code>true</code> if this {@link UIProvider} is running a Graphical User Interface.
    */
   boolean isGUI();

   /**
    * @return <code>true</code> if this {@link UIProvider} is being executed in an embedded environment (eg: as a Shell
    *         in IDE or as part of an application)
    */
   boolean isEmbedded();

   /**
    * @return the {@link UIOutput} used to display messages during a UI interaction
    */
   UIOutput getOutput();

   /**
    * @return the {@link UIDesktop} associated with this {@link UIProvider}.
    */
   UIDesktop getDesktop();
}