/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * The command line shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Shell extends UIProvider
{
   /**
    * Initializes the shell
    * 
    * @param settings
    * @param initialSelection
    */
   public void init(Settings settings);

   /**
    * Sets the initial selection for this shell
    * 
    * @param initialSelection
    */
   public void setInitialSelection(UISelection<?> initialSelection);

   /**
    * Get the native {@link Console} object.
    */
   public Console getConsole();

}
