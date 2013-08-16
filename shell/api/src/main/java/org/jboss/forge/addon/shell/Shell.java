/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.Closeable;

import org.jboss.aesh.console.Console;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * The command line shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Shell extends UIProvider, Closeable
{
   /**
    * Gets the current {@link UISelection} of this {@link Shell}.
    */
   UISelection<?> getCurrentSelection();

   /**
    * Sets the current {@link UISelection} for this {@link Shell}
    */
   public void setCurrentSelection(UISelection<?> initialSelection);

   /**
    * Get the native {@link Console} object.
    */
   public Console getConsole();
}
